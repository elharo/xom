<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <root xmlns="http://www.root.com" xmlns:ex="http://www.example.com/" xmlns:unused="htp://www.unused.com/">
      <foo>ex:test</foo>
      <red xmlns="http://www.somehwere.com"/>
      a
      <pre:foo xmlns:pre="http://www.output.org/" xmlns="">
      a
        <hello pre:link="">data</hello>
        a
      </pre:foo>
      a
      <ex:test></ex:test>
      a
    </root>
  </xsl:template>



  
</xsl:stylesheet>
