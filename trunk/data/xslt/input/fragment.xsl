<?xml version="1.0"?> 
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
				
  <!-- test a stylesheet that generates a document fragment -->
				
  <xsl:template match="/">
    <element1>some data and <content/> for a test</element1>
    <element2>Remember, the XSLT processor is going to strip out the literal white space</element2>
    <element3>some data and <content/> for a <!--test--></element3>
	<element4/>
	<xsl:comment>test</xsl:comment>
	<xsl:processing-instruction name="test">PIs are not treated as literals in XSLT?</xsl:processing-instruction>
  </xsl:template>

</xsl:stylesheet>
