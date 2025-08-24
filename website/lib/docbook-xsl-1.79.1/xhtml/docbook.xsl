<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                version='1.0'>
  
  <xsl:output method="xml" 
              encoding="UTF-8"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>

  <xsl:template match="/">
    <html>
      <head>
        <title><xsl:value-of select="//title[1]"/></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="article">
    <div class="article">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="title">
    <h1><xsl:apply-templates/></h1>
  </xsl:template>

  <xsl:template match="sect1/title">
    <h2><xsl:apply-templates/></h2>
  </xsl:template>

  <xsl:template match="sect2/title">
    <h3><xsl:apply-templates/></h3>
  </xsl:template>

  <xsl:template match="sect1 | sect2 | sect3">
    <div class="{name()}">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="para">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="programlisting">
    <pre><code><xsl:apply-templates/></code></pre>
  </xsl:template>

  <xsl:template match="emphasis">
    <em><xsl:apply-templates/></em>
  </xsl:template>

  <xsl:template match="filename | classname | methodname">
    <code><xsl:apply-templates/></code>
  </xsl:template>

  <xsl:template match="ulink">
    <a href="{@url}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="itemizedlist">
    <ul><xsl:apply-templates select="listitem"/></ul>
  </xsl:template>

  <xsl:template match="orderedlist">
    <ol><xsl:apply-templates select="listitem"/></ol>
  </xsl:template>

  <xsl:template match="listitem">
    <li><xsl:apply-templates/></li>
  </xsl:template>

  <xsl:template match="articleinfo"/>

  <xsl:template match="text()">
    <xsl:copy/>
  </xsl:template>

</xsl:stylesheet>