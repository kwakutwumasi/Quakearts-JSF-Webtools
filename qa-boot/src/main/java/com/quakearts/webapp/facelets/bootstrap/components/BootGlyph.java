/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.webapp.facelets.bootstrap.components;

import java.util.HashMap;

import javax.faces.component.UIOutput;

import com.quakearts.webapp.facelets.util.ObjectExtractor;

public class BootGlyph extends UIOutput {
	private static final HashMap<String, String> VALIDVALUESMAP = new HashMap<String, String>();
	private static final String EMPTY = "";
	private static final String[] VALIDVALUES=new String[]{"asterisk",
			"plus", "euro", "eur", "minus", "cloud", "envelope", "pencil",
			"glass", "music", "search", "heart", "star", "star-empty", "user",
			"film", "th-large", "th", "th-list", "ok", "remove", "zoom-in",
			"zoom-out", "off", "signal", "cog", "trash", "home", "file",
			"time", "road", "download-alt", "download", "upload", "inbox",
			"play-circle", "repeat", "refresh", "list-alt", "lock", "flag",
			"headphones", "volume-off", "volume-down", "volume-up", "qrcode",
			"barcode", "tag", "tags", "book", "bookmark", "print", "camera",
			"font", "bold", "italic", "text-height", "text-width",
			"align-left", "align-center", "align-right", "align-justify",
			"list", "indent-left", "indent-right", "facetime-video", "picture",
			"map-marker", "adjust", "tint", "edit", "share", "check", "move",
			"step-backward", "fast-backward", "backward", "play", "pause",
			"stop", "forward", "fast-forward", "step-forward", "eject",
			"chevron-left", "chevron-right", "plus-sign", "minus-sign",
			"remove-sign", "ok-sign", "question-sign", "info-sign",
			"screenshot", "remove-circle", "ok-circle", "ban-circle",
			"arrow-left", "arrow-right", "arrow-up", "arrow-down", "share-alt",
			"resize-full", "resize-small", "exclamation-sign", "gift", "leaf",
			"fire", "eye-open", "eye-close", "warning-sign", "plane",
			"calendar", "random", "comment", "magnet", "chevron-up",
			"chevron-down", "retweet", "shopping-cart", "folder-close",
			"folder-open", "resize-vertical", "resize-horizontal", "hdd",
			"bullhorn", "bell", "certificate", "thumbs-up", "thumbs-down",
			"hand-right", "hand-left", "hand-up", "hand-down",
			"circle-arrow-right", "circle-arrow-left", "circle-arrow-up",
			"circle-arrow-down", "globe", "wrench", "tasks", "filter",
			"briefcase", "fullscreen", "dashboard", "paperclip", "heart-empty",
			"link", "phone", "pushpin", "usd", "gbp", "sort",
			"sort-by-alphabet", "sort-by-alphabet-alt", "sort-by-order",
			"sort-by-order-alt", "sort-by-attributes",
			"sort-by-attributes-alt", "unchecked", "expand", "collapse-down",
			"collapse-up", "log-in", "flash", "log-out", "new-window",
			"record", "save", "open", "saved", "import", "export", "send",
			"floppy-disk", "floppy-saved", "floppy-remove", "floppy-save",
			"floppy-open", "credit-card", "transfer", "cutlery", "header",
			"compressed", "earphone", "phone-alt", "tower", "stats",
			"sd-video", "hd-video", "subtitles", "sound-stereo", "sound-dolby",
			"sound-5-1", "sound-6-1", "sound-7-1", "copyright-mark",
			"registration-mark", "cloud-download", "cloud-upload",
			"tree-conifer", "tree-deciduous", "cd", "save-file", "open-file",
			"level-up", "copy", "paste", "alert", "equalizer", "king", "queen",
			"pawn", "bishop", "knight", "baby-formula", "tent", "blackboard",
			"bed", "apple", "erase", "hourglass", "lamp", "duplicate",
			"piggy-bank", "scissors", "bitcoin", "yen", "ruble", "scale",
			"ice-lolly", "ice-lolly-tasted", "education", "option-horizontal",
			"option-vertical", "menu-hamburger", "modal-window", "oil",
			"grain", "sunglasses", "text-size", "text-color",
			"text-background", "object-align-top", "object-align-bottom",
			"object-align-horizontal", "object-align-left",
			"object-align-vertical", "object-align-right", "triangle-right",
			"triangle-left", "triangle-bottom", "triangle-top", "console",
			"superscript", "subscript", "menu-left", "menu-right", "menu-down",
			"menu-up"};
	static{
		for(String validValue:VALIDVALUES){
			VALIDVALUESMAP.put(validValue, EMPTY);
		}
	}
	
	public static final String COMPONENT_FAMILY="com.quakearts.bootstrap.glyph";
	public static final String RENDERER_TYPE="com.quakearts.bootstrap.glyph.renderer";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public boolean isValid(String type){
		return VALIDVALUESMAP.get(type)!=null;
	}

	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}

	@Override
	public void setRendererType(String rendererType) {
	}
	
	public String get(String attribute) {
		String attributeValue = ObjectExtractor
				.extractString(getValueExpression(attribute), getFacesContext()
						.getELContext());
		if (attributeValue == null)
			attributeValue = (String) getAttributes().get(attribute);

		return attributeValue;
	}
}
