/*
 * Copyright (C) 2006-2007 Mindquarry GmbH, All Rights Reserved
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
 
 /*
    IconSelect - extends ComboBox.
    Provides a select-type widget (select from a list)
    With Icons on each row.
 */
 
dojo.provide("mindquarry.widget.IconSelect");
dojo.require("dojo.widget.Select");

dojo.widget.defineWidget(
    "mindquarry.widget.IconSelect",
    dojo.widget.html.Select,
    {

		// properties from the tag
		iconprefix: "", 
		iconsuffix: ".png",
		name: "",
		filter: true,
		size: 22,

		// override the drawing of the list, so we can add icons.
		openResultList: function(results){
			this.clearResultList();
			dojo.debug("got things to add to the list: " + results.length);
			if(!results.length){
				this.hideResultList();
			}
	
			if(	(this.autoComplete)&&
				(results.length)&&
				(!this._prev_key_backspace)&&
				(this.textInputNode.value.length > 0)){
				var cpos = this.getCaretPos(this.textInputNode);
				// only try to extend if we added the last character at the end of the input
				if((cpos+1) > this.textInputNode.value.length){
					// only add to input node as we would overwrite Capitalisation of chars
					this.textInputNode.value += results[0][0].substr(cpos);
					// build a new range that has the distance from the earlier
					// caret position to the end of the first string selected
					this.setSelectedRange(this.textInputNode, cpos, this.textInputNode.value.length);
				}
			}
	
			var even = true;
			while(results.length){
				var tr = results.shift();
				if(tr){
					var td = document.createElement("div");
					td.appendChild(document.createTextNode(tr[0]));
					td.setAttribute("resultName", tr[0]);
					td.setAttribute("resultValue", tr[1]);
					td.className = "mindquarrySelect dojoComboBoxItem "+((even) ? "dojoComboBoxItemEven" : "dojoComboBoxItemOdd");
					td.style.backgroundImage = "url(" + this.iconprefix + "/" + this.size + "/" + this.name + "/" + tr[1] + this.iconsuffix + ")";
					td.style.backgroundRepeat = "no-repeat";
					td.style.paddingLeft = (this.size + 2) + "px";
					td.style.margin = "1px";
					even = (!even);
					this.optionsListNode.appendChild(td);
					dojo.event.connect(td, "onmouseover", this, "itemMouseOver");
					dojo.event.connect(td, "onmouseout", this, "itemMouseOut");
				}
			}
	
			// show our list (only if we have content, else nothing)
			this.showResultList();
		},

		setAllValues: function(value1, value2){
			dojo.debug("Calling = setAllValues : " + value1 + " = " + value2);
			this.setLabel(value1);
			this.setValue(value2);
		},
		
		handleArrowClick: function() {
    		this._handleBlurTimer(true, 0);
    		this.tryFocus();
    		if(this._result_list_open){
    			this.hideResultList();
    		}else{
    			this.startSearch("");
    		}
        }

});
