<?xml version='1.0'?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY RE "&#10;">
<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                version='1.0'>

<xsl:import href="/opt/xml/docbook-xsl/xhtml/docbook.xsl"/>

  <xsl:output method="xml" encoding="UTF-8"/>
<xsl:param name="xref.with.number.and.title" select="0"/>
               

<xsl:variable name="arg.choice.opt.open.str"></xsl:variable>
<xsl:variable name="arg.choice.opt.close.str"></xsl:variable>

<xsl:template name="user.head.content">
  <meta name="description"
        content="A quick introduction to XOM by Elliotte Rusty Harold" />
  <link rel="icon" type="image/x-icon" href="favicon.ico" />
</xsl:template>

<xsl:template name="user.footer.navigation" xmlns:dt="http://xsltsl.org/date-time">
<hr/>
<table width="100%" summary="Cafe con Leche footer">
<tr>
<td width="34%" align="left">Copyright 2002-2016 Elliotte Rusty Harold</td>
<td width="32%" align="center"><a href="mailto:elharo@ibiblio.org">elharo@ibiblio.org</a></td>
<td width="34%" align="right">Last Modified 
<xsl:call-template name="get-month-name">
  <xsl:with-param name="month"><xsl:value-of select="number(substring(@revision, 5, 2))"/></xsl:with-param>
</xsl:call-template>
<xsl:text> </xsl:text>
<xsl:value-of select="substring(@revision, 7, 2)"/>,
<xsl:value-of select="substring(@revision, 1, 4)"/>
</td>
</tr>
<tr>
<td width="34%" align="left"></td>
<td width="32%" align="center"><a href="http://www.cafeconleche.org/">Up To Cafe con Leche</a></td>
<td width="34%" align="right"></td>
</tr>
</table>
  <!-- need to add a template to make last modified date more human readable -->

</xsl:template>

  <xsl:template name="get-month-name">
    <xsl:param name="month"/>

    <xsl:choose>
      <xsl:when test="$month = 1">January</xsl:when>
      <xsl:when test="$month = 2">February</xsl:when>
      <xsl:when test="$month = 3">March</xsl:when>
      <xsl:when test="$month = 4">April</xsl:when>
      <xsl:when test="$month = 5">May</xsl:when>
      <xsl:when test="$month = 6">June</xsl:when>
      <xsl:when test="$month = 7">July</xsl:when>
      <xsl:when test="$month = 8">August</xsl:when>
      <xsl:when test="$month = 9">September</xsl:when>
      <xsl:when test="$month = 10">October</xsl:when>
      <xsl:when test="$month = 11">November</xsl:when>
      <xsl:when test="$month = 12">December</xsl:when>
      <xsl:otherwise>error: <xsl:value-of select="$month"/></xsl:otherwise>
    </xsl:choose>

  </xsl:template>

<xsl:template match="fieldsynopsis" mode="java">
  <code class="{name(.)}">
    <xsl:if test="parent::classsynopsis">
      <xsl:if test="position() = 1">
        <br/>
      </xsl:if>
    <xsl:text>&nbsp;&nbsp;</xsl:text>
    </xsl:if>
    <xsl:apply-templates mode="java"/>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<xsl:template match="methodname" mode="java">
  <span class="{name(.)}" style="font-weight: bold">
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="parameter" mode="java">
  <span class="{name(.)}" style="font-style: italic">
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template mode="java"
  match="constructorsynopsis|destructorsynopsis|methodsynopsis">
  <xsl:variable name="modifiers" select="modifier"/>
  <xsl:variable name="notmod" select="*[name(.) != 'modifier']"/>
  <xsl:variable name="decl">
    <xsl:if test="parent::classsynopsis">
      <xsl:if test="position() = 1">
        <br/>
      </xsl:if>
      <xsl:text>&nbsp;&nbsp;</xsl:text>
    </xsl:if>
    <xsl:apply-templates select="$modifiers" mode="java"/>

    <!-- type -->
    <xsl:if test="name($notmod[1]) != 'methodname'">
      <xsl:apply-templates select="$notmod[1]" mode="java"/>
    </xsl:if>

    <xsl:apply-templates select="methodname" mode="java"/>
  </xsl:variable>

  <code class="{name(.)}">
    <xsl:copy-of select="$decl"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="methodparam" mode="java">
      <xsl:with-param name="indent" select="string-length($decl)"/>
    </xsl:apply-templates>
    <xsl:text>)</xsl:text>
    <xsl:if test="exceptionname">
      <br/>
      <xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;throws&nbsp;</xsl:text>
      <xsl:apply-templates select="exceptionname" mode="java"/>
    </xsl:if>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<xsl:template match="classname" mode="javasuperclass">
  <xsl:if test="name(preceding-sibling::*[1]) = 'classname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span class="{name(.)}"><xsl:apply-templates mode="java"/></span>
</xsl:template>

<xsl:template match="classname" mode="java">
  <span class="{name(.)}">class <xsl:apply-templates mode="java"/></span>
</xsl:template>


  <xsl:template match="markup">
    <xsl:call-template name="inline.monoseq"/>
  </xsl:template>

  <xsl:template match="footnote" mode="footnote.number">
    <xsl:number level="any" format="1" from="chapter"/>
  </xsl:template>

<!-- change font in admonitions -->
<xsl:param name="admon.style">
  <xsl:text>margin-left: 0.5in; margin-right: 0.5in; font-family: Helvetica, Arial, sans</xsl:text>
</xsl:param>

<!-- better formatting for sidebars -->

<!-- ==================================================================== -->

<xsl:template match="sidebar">
  <table class="{name(.)}"  border="0">
  <tr>
  <td width="10%"/>
  <td width="80%" style="font-family: Helvetica, Arial, sans" cellpadding="5">
    <xsl:call-template name="anchor"/>
    <xsl:apply-templates/>
  </td>
  <td width="10%"/>
  </tr>
  </table>
</xsl:template>

<xsl:template match="sidebar/title">
  <p class="title" style="font-size: larger; font-weight: bold; text-align: center">
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="methodparam" mode="java">
  <xsl:param name="indent">0</xsl:param>
  <xsl:if test="position() &gt; 1">
    <xsl:text>, </xsl:text>
    <xsl:if test="$indent &gt; 0">
      <xsl:call-template name="copy-string">
	<xsl:with-param name="string"> </xsl:with-param>
	<xsl:with-param name="count" select="$indent + 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:if>
  <span class="{name(.)}">
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

</xsl:stylesheet>

