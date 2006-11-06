<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xhtml="http://www.w3.org/1999/xhtml"
        xmlns:xlink="http://www.w3.org/1999/xlink">
        
	<xsl:import href="block:/xslt/contextpath.xsl" />
		
    <xsl:param name="editDocumentLink" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="xhtml:head|head">
		<head>
			<!-- copy existing link/script stuff -->
			<xsl:apply-templates />
			
			<link rel="stylesheet" href="{$pathToBlock}css/task-view.css" 
				media="screen,projection" type="text/css" />
		</head>
	</xsl:template>

	<xsl:template match="form[@id='ductform' and @state='output']">
		<h1>Details for Task: <xsl:value-of select="/html/head/title" /></h1>
		<div class="nifty">
			<div id="actions">
				<a class="edit_task_button" href="{$editDocumentLink}">Edit Task</a>
			</div>
			
			<xsl:copy-of select="." />
						
			<div id="footbar">
				<a href="./">List tasks</a>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>
