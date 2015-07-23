package com.quakearts.webapp.security.auth;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import com.quakearts.webapp.security.auth.util.AttemptChecker;
import com.quakearts.webapp.security.auth.webserviceclient.Authenticator;
import com.quakearts.webapp.security.auth.webserviceclient.LoginBean;
import com.quakearts.webapp.security.auth.webserviceclient.Type;

public class WebServiceLoginModule implements LoginModule {
	private Subject subject;
    private Group rolesgrp;
	private com.quakearts.webapp.security.auth.webserviceclient.Subject profile;
	private String domain,webserviceLocation,webserviceBaseURL,rolesgrpname;
	private String[] defaultroles;
	private boolean loginOk = false, use_first_pass=false;
	private AttemptChecker checker;
	@SuppressWarnings("rawtypes")
	private Map sharedState;
	private static final Logger log = Logger.getLogger(WebServiceLoginModule.class);
	private CallbackHandler callbackHandler;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		this.subject = subject;
		this.sharedState = sharedState;
		this.callbackHandler = callbackHandler;
		domain = (String) options.get("login.domain");
		webserviceBaseURL = (String) options.get("login.service.url");
		webserviceLocation = (String) options.get("login.service.location");
        String defaultroles_str = (String) options.get("defaultroles");
        
        if (rolesgrpname == null)
         rolesgrpname = "Roles";
        
        if(defaultroles_str != null){
        	defaultroles = defaultroles_str.split(";");
        	for(int i=0;i<defaultroles.length;i++)
        		defaultroles[i] = defaultroles[i].trim();
        }
        
        String maxAttempts_str = (String) options.get("max_try_attempts");
        String lockoutTime_str = (String) options.get("lockout_time");
        
        if(maxAttempts_str == null && lockoutTime_str == null){
        	checker = AttemptChecker.getChecker(domain);
        }else{
	        int maxAttempts, lockoutTime;
	        try {
				maxAttempts = Integer.parseInt(maxAttempts_str);
			} catch (Exception e) {
				maxAttempts = 4;
			}
			
			try {
				lockoutTime = Integer.parseInt(lockoutTime_str);
			} catch (Exception e) {
				lockoutTime = 3600000;
			}
        	AttemptChecker.createChecker(domain, maxAttempts, lockoutTime);
        	checker = AttemptChecker.getChecker(domain);
        }
        
        Object use_first_pass_val = options.get("use_first_pass");
        if(use_first_pass_val != null?use_first_pass_val instanceof String:false)
            use_first_pass = Boolean.parseBoolean((String)use_first_pass_val);
        
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean login() throws LoginException {
		Authenticator authenticator;
		String username = null, password = null;
		try{
			
            Callback[] callbacks = new Callback[2];

            if(use_first_pass){
                if(sharedState != null){
                    log.trace("Using first pass....");
                    Object loginDN_val = sharedState.get("javax.security.auth.login.name");
                    Object password_val = sharedState.get("javax.security.auth.login.password");
                    username = (loginDN_val!=null && loginDN_val instanceof Principal)?((Principal) loginDN_val).getName():null;
                    password = (password_val!=null && password_val instanceof char[])?new String((char[]) password_val):null;
                }
            }
            
            if(!use_first_pass || username==null || password==null){
                NameCallback name = new NameCallback("Enter your username");
                PasswordCallback pass = new PasswordCallback("Enter your password:",false);           
                callbacks[0] = name;
                callbacks[1] = pass;

                try {
                    log.trace("Handling callback....");
                    callbackHandler.handle(callbacks);
                } catch (UnsupportedCallbackException e) {
                    throw new DirectoryLoginException("Callback is not supported", e);
                } catch (IOException e) {
                    throw new DirectoryLoginException("IOException during call back", e);
                }
                
                username = name.getName()==null? name.getDefaultName():name.getName();
                password = (new String(pass.getPassword()));
                
                if (sharedState != null){
                    log.trace("Storing state....");
                    UserPrincipal shareduser = new UserPrincipal(username);
                    sharedState.put("javax.security.auth.login.name", shareduser);
                    char[] sharedpass = new String(password).toCharArray();
                    sharedState.put("javax.security.auth.login.password", sharedpass);
                }
            }
                       
            if(username == null || password == null)
                throw new LoginException("Login/Password is null.");
            
            if(checker.isLocked(username))
                throw new LoginException("Account is lockedout.");
		
			if(webserviceBaseURL !=null && webserviceLocation!=null)
				authenticator = new Authenticator(new URL(webserviceBaseURL+"/"+webserviceLocation), new QName("http://security.jboss.quakearts.com/", "authenticator"));
			else
				authenticator = new Authenticator();
			
			LoginBean loginBean = authenticator.getLoginBeanPort();
			
			HostnameVerifier oldverifier = HttpsURLConnection.getDefaultHostnameVerifier();
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return hostname.equals(webserviceBaseURL);
				}
			});
			profile = loginBean.authenticate(domain, username, password);
			checker.reset(username);
			HttpsURLConnection.setDefaultHostnameVerifier(oldverifier);
			
			loginOk = profile.getResult().getType() == Type.SUCCESS;
			return loginOk;
		}catch (Exception e) {
			throw new LoginException(e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean commit() throws LoginException {
		if(loginOk){
            Set<Principal> principalset = subject.getPrincipals();            
            if(use_first_pass){
        		log.trace("Fetching already existing roles group...");
				for (Iterator i = principalset.iterator(); i.hasNext();) {
					Object obj = i.next();
					if (obj instanceof Group && ((Group) obj).getName().equals(rolesgrpname)) {
						rolesgrp = (Group) obj;
                        log.trace("Found existing roles group: "+rolesgrp.getName());
						break;
					}
				}				
        	}
        	if(rolesgrp==null)
        		rolesgrp = new DirectoryRoles(rolesgrpname);

			List<com.quakearts.webapp.security.auth.webserviceclient.Subject.Principal> principals = profile.getPrincipal();
			for(com.quakearts.webapp.security.auth.webserviceclient.Subject.Principal principal:principals){
				if(principal.getName().equals(UserPrincipal.class.getName()))
					rolesgrp.addMember(new UserPrincipal(principal.getValue()));
				else if(principal.getName().equals(NamePrincipal.class.getName()))
					rolesgrp.addMember(new NamePrincipal(principal.getValue()));					
				else if(principal.getName().equals(UnitPrincipal.class.getName()))
					rolesgrp.addMember(new UnitPrincipal(principal.getValue()));					
				else if(principal.getName().equals(DeptPrincipal.class.getName()))
					rolesgrp.addMember(new DeptPrincipal(principal.getValue()));					
				else if(principal.getName().equals(BranchPrincipal.class.getName()))
					rolesgrp.addMember(new BranchPrincipal(principal.getValue()));
				else if(principal.getName().equals(StaffNumberPrincipal.class.getName()))
					rolesgrp.addMember(new StaffNumberPrincipal(principal.getValue()));					
				else if(principal.getName().equals(GradePrincipal.class.getName()))
					rolesgrp.addMember(new GradePrincipal(principal.getValue()));					
				else if(principal.getName().equals(PositionPrincipal.class.getName()))
					rolesgrp.addMember(new PositionPrincipal(principal.getValue()));
				else if(principal.getName().equals(EmailPrincipal.class.getName()))
					rolesgrp.addMember(new EmailPrincipal(principal.getValue()));
				else
					rolesgrp.addMember(new OtherPrincipal(principal.getValue(), principal.getName()));				
			}
			
			//Add default roles
			if (defaultroles != null)
				for (String role : defaultroles) {
					OtherPrincipal principal = new OtherPrincipal(role, "default");
					rolesgrp.addMember(principal);
				}
			
			principalset.add(rolesgrp);
		}
		if(log.isTraceEnabled()){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Commiting user with profile: ").append("\n");
			Object obj=null;
			for(Enumeration<? extends Principal> members= rolesgrp.members();members.hasMoreElements();obj = members.nextElement()){
				if(obj!=null)
					buffer.append(obj.getClass().getName()).append(":= ").append(((Principal)obj).getName()).append("\n");
			}
			log.trace(buffer);
		}
		return loginOk;
	}

	@Override
	public boolean abort() throws LoginException {
		subject = null;
		profile = null;
		loginOk = false;
		rolesgrp = null;
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		subject = null;
		profile = null;
		loginOk = false;
		rolesgrp = null;
		return true;
	}

}
