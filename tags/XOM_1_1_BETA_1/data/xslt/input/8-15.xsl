<?xml version="1.0"?> <xsl:stylesheet version="1.0"                 xmlns="http://www.w3.org/1999/xhtml"                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                xmlns:pe="http://namespaces.oreilly.com/people">  <xsl:output method="xml" indent="no"/>  <xsl:template match="pe:people">    <html>      <head><title>Famous Scientists</title></head>      <body>        <xsl:apply-templates/>      </body>    </html>  </xsl:template>  <xsl:template match="pe:name">    <p><xsl:value-of select="pe:last_name"/>,     <xsl:value-of select="pe:first_name"/></p>  </xsl:template>  <xsl:template match="pe:person">    <xsl:apply-templates select="pe:name"/>  </xsl:template></xsl:stylesheet>