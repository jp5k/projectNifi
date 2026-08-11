<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <!--
    Reshapes a single <priceUpdate> element (produced upstream by NiFi's
    ConvertRecord: JSON -> XML) into a <priceReport>: symbol/classification
    move to attributes, a human-readable headline is composed, and
    classification gets a friendly label. Demonstrates the classic XSLT moves:
    element-to-attribute, string concatenation, and conditional branching.
  -->
  <xsl:template match="/priceUpdate">
    <priceReport symbol="{symbol}" classification="{classification}">
      <headline>
        <xsl:value-of select="name"/>
        <xsl:text> (</xsl:text>
        <xsl:value-of select="symbol"/>
        <xsl:text>) — £</xsl:text>
        <xsl:value-of select="price"/>
      </headline>
      <details>
        <capturedAt><xsl:value-of select="timestamp"/></capturedAt>
        <volume><xsl:value-of select="volume"/></volume>
        <visibility>
          <xsl:choose>
            <xsl:when test="classification='PUBLIC'">Public</xsl:when>
            <xsl:when test="classification='INTERNAL'">Internal</xsl:when>
            <xsl:when test="classification='RESTRICTED'">Restricted</xsl:when>
            <xsl:otherwise><xsl:value-of select="classification"/></xsl:otherwise>
          </xsl:choose>
        </visibility>
      </details>
    </priceReport>
  </xsl:template>
</xsl:stylesheet>
