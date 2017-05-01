package com.quakearts.webapp.security.rest.cdi;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.quakearts.webapp.security.rest.SecurityContext;
import com.quakearts.webapp.security.rest.exception.SecurityContextException;

@RequireAuthorization @Interceptor
public class AuthorizationInterceptor {

	@AroundInvoke
	public Object authorizeSubject(InvocationContext context) throws Exception {
		RequireAuthorization authorization = context.getMethod().getAnnotation(RequireAuthorization.class);
		if(authorization==null)
			authorization = context.getMethod().getDeclaringClass().getAnnotation(RequireAuthorization.class);
	
		if(authorization==null)
			throw new SecurityContextException("No authorization parameter found to check for roles");

		SecurityContext securityContext = SecurityContext.getSecurityContext();
		if(securityContext.getSubject()==null)
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
		
		boolean proceed = true;
		if(authorization.allow().length>0){
			proceed = false;
			List<String> allowed = Arrays.asList(authorization.allow());
			
			for(Principal principal:securityContext.getSubject().getPrincipals()){
				if(allowed.contains(principal.getName())){
					proceed = true;
					break;
				}
			}
		}

		if(proceed && authorization.deny().length>0){
			List<String> deny = Arrays.asList(authorization.allow());
			
			for(Principal principal:securityContext.getSubject().getPrincipals()){
				if(deny.contains(principal.getName())){
					proceed = false;
					break;
				}
			}
		}
		
		if(proceed)
			return context.proceed();
		else
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
	}
}