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
  xmlns:email="http://apache.org/cocoon/transformation/sendmail"
	xmlns:source="http://apache.org/cocoon/source/1.0">
  
  <xsl:template match="email:sendmail/conversation">
    <email:subject><xsl:value-of select="title" /></email:subject>
    <xsl:apply-templates select="subscribers/subscriber[@type='email']" />
  </xsl:template>
  
  <xsl:template match="subscriber">
    <xsl:variable name="id" select="normalize-space(.)" />
    <email:to><xsl:value-of select="../../../users/user[normalize-space(id)=$id]/email" /></email:to>
  </xsl:template>
  
  <xsl:template match="email:sendmail/users">
  
  </xsl:template>
  
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
