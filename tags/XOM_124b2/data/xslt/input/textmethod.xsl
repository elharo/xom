<?xml version="1.0"?> 
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
				
  <!-- test a stylesheet that use method="text" -->
  <xsl:output method="text"/>
				
				
  <xsl:template match="/">
    <element1>12345</element1>
    <element2>67890</element2>
    <element3/>
	<element4>0987654321</element4>
	<xsl:comment>test</xsl:comment>
	<xsl:processing-instruction name="test">PIs are not treated as literals in XSLT?</xsl:processing-instruction>
  </xsl:template>

</xsl:stylesheet>
