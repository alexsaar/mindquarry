<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fi="http://apache.org/cocoon/forms/1.0#instance"
	exclude-result-prefixes="fi">

  <!-- styling of the page -->
  <xsl:include href="resource://org/apache/cocoon/forms/resources/forms-page-styling.xsl"/>
  <!-- styling of the widgets -->
  
  <xsl:include href="resource://org/apache/cocoon/forms/resources/forms-advanced-field-styling.xsl"/>

  <!--  styling of dojo editor (from lenya) -->
  <xsl:include href="dojo-editor.xsl"/>
   
  <!-- resources directory for Dojo js, css and the like -->
  <xsl:param name="resources-uri">resources</xsl:param>

  <xsl:template match="head">
  	<xsl:copy>
      <xsl:apply-templates select="." mode="forms-page"/>
      <xsl:apply-templates select="." mode="forms-field"/>
      <xsl:apply-templates select="." mode="forms-dojoarea"/>
      <xsl:apply-templates/>
	</xsl:copy>
  </xsl:template>

  <xsl:template match="body">
  	<xsl:copy>
      <xsl:apply-templates select="." mode="forms-page"/>
      <xsl:apply-templates select="." mode="forms-field"/>
      <xsl:apply-templates/>
      </xsl:copy>
  </xsl:template>
  

</xsl:stylesheet>