<?xml version="1.0" encoding="UTF-8"?>
<!--
    Copyright (C) 2006-2007 Mindquarry GmbH, All Rights Reserved
    
    The contents of this file are subject to the Mozilla Public License
    Version 1.1 (the "License"); you may not use this file except in
    compliance with the License. You may obtain a copy of the License at
    http://www.mozilla.org/MPL/
    
    Software distributed under the License is distributed on an "AS IS"
    basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
    License for the specific language governing rights and limitations
    under the License.
-->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:collection="http://apache.org/cocoon/collection/1.0"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		exclude-result-prefixes="collection">

    <xsl:param name="basePath" />
    <xsl:param name="teamspace" />

    <xsl:template match="/collection:collection">
    	<conversations xml:base="{$basePath}{$teamspace}/" xlink:href="{$teamspace}">
    		<xsl:apply-templates />
    	</conversations>
    </xsl:template>

    <xsl:template match="collection:collection">
		  <conversation xlink:href="{@name}/" id="{@name}"/>
    </xsl:template>

</xsl:stylesheet>
