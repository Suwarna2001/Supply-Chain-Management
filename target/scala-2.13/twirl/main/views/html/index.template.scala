
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

object index extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(title: String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.17*/("""

"""),format.raw/*3.1*/("""<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>"""),_display_(/*9.11*/title),format.raw/*9.16*/("""</title>
  <style>
    body """),format.raw/*11.10*/("""{"""),format.raw/*11.11*/("""
      """),format.raw/*12.7*/("""background-color: #d24ffe;
      font-family: Arial, sans-serif;
      color: #ffffff;
      margin: 0;
      padding: 0;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    """),format.raw/*21.5*/("""}"""),format.raw/*21.6*/("""

    """),format.raw/*23.5*/(""".container """),format.raw/*23.16*/("""{"""),format.raw/*23.17*/("""
      """),format.raw/*24.7*/("""max-width: 900px;
      margin: 20px;
      padding: 20px;
      background-color: rgba(255, 255, 255, 0.1);
      border-radius: 20px;
      text-align: center;
    """),format.raw/*30.5*/("""}"""),format.raw/*30.6*/("""

    """),format.raw/*32.5*/("""h1 """),format.raw/*32.8*/("""{"""),format.raw/*32.9*/("""
      """),format.raw/*33.7*/("""font-size: 36px;
      margin-bottom: 20px;
    """),format.raw/*35.5*/("""}"""),format.raw/*35.6*/("""

    """),format.raw/*37.5*/("""h2 """),format.raw/*37.8*/("""{"""),format.raw/*37.9*/("""
      """),format.raw/*38.7*/("""font-size: 28px;
      margin-bottom: 10px;
    """),format.raw/*40.5*/("""}"""),format.raw/*40.6*/("""

    """),format.raw/*42.5*/("""p """),format.raw/*42.7*/("""{"""),format.raw/*42.8*/("""
      """),format.raw/*43.7*/("""font-size: 20px;
      margin-bottom: 20px;
    """),format.raw/*45.5*/("""}"""),format.raw/*45.6*/("""

    """),format.raw/*47.5*/(""".file-upload """),format.raw/*47.18*/("""{"""),format.raw/*47.19*/("""
      """),format.raw/*48.7*/("""margin-bottom: 20px;
    """),format.raw/*49.5*/("""}"""),format.raw/*49.6*/("""

    """),format.raw/*51.5*/(""".file-upload label """),format.raw/*51.24*/("""{"""),format.raw/*51.25*/("""
      """),format.raw/*52.7*/("""display: block;
      font-size: 20px;
      margin-bottom: 5px;
    """),format.raw/*55.5*/("""}"""),format.raw/*55.6*/("""

    """),format.raw/*57.5*/(""".file-upload input[type="file"] """),format.raw/*57.37*/("""{"""),format.raw/*57.38*/("""
      """),format.raw/*58.7*/("""display: block;
      margin: 0 auto;
      font-size: 16px;
    """),format.raw/*61.5*/("""}"""),format.raw/*61.6*/("""

    """),format.raw/*63.5*/(""".submit-button """),format.raw/*63.20*/("""{"""),format.raw/*63.21*/("""
      """),format.raw/*64.7*/("""display: block;
      margin: 0 auto;
      padding: 10px 20px;
      font-size: 20px;
      border: none;
      border-radius: 5px;
      background-color: #4CAF50;
      color: white;
      cursor: pointer;
      transition: background-color 0.3s ease;
    """),format.raw/*74.5*/("""}"""),format.raw/*74.6*/("""

    """),format.raw/*76.5*/(""".submit-button:hover """),format.raw/*76.26*/("""{"""),format.raw/*76.27*/("""
      """),format.raw/*77.7*/("""background-color: #45a049;
    """),format.raw/*78.5*/("""}"""),format.raw/*78.6*/("""
  """),format.raw/*79.3*/("""</style>
</head>

<body>
<div class="container">
  <h1>LogiSync</h1>
  <h2>Streamlining Supply Chains</h2>
  <p>Welcome to SyncLogix, where supply chain mastery meets unparalleled control. With our platform, Controlled Distribution Managers orchestrate seamless logistics, optimizing every movement for success.</p>
  <form action="/uploadJson" method="POST" enctype="multipart/form-data">
    <div class="file-upload">
      <label for="order-json-file">Upload Order Details JSON File</label>
      <input type="file" id="order-json-file" name="orderJsonFile" accept=".json">
    </div>
    <div class="file-upload">
      <label for="supplier-json-file">Upload Supplier Details JSON File</label>
      <input type="file" id="supplier-json-file" name="supplierJsonFile" accept=".json">
    </div>
    <button type="submit" class="submit-button">Submit</button>
  </form>
</div>
</body>

</html>

"""))
      }
    }
  }

  def render(title:String): play.twirl.api.HtmlFormat.Appendable = apply(title)

  def f:((String) => play.twirl.api.HtmlFormat.Appendable) = (title) => apply(title)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/index.scala.html
                  HASH: 784c7cdad06f0c5c753947c88fa28d6fadaebb78
                  MATRIX: 729->1|839->16|869->20|1050->175|1075->180|1133->210|1162->211|1197->219|1459->454|1487->455|1522->463|1561->474|1590->475|1625->483|1824->655|1852->656|1887->664|1917->667|1945->668|1980->676|2057->726|2085->727|2120->735|2150->738|2178->739|2213->747|2290->797|2318->798|2353->806|2382->808|2410->809|2445->817|2522->867|2550->868|2585->876|2626->889|2655->890|2690->898|2743->924|2771->925|2806->933|2853->952|2882->953|2917->961|3016->1033|3044->1034|3079->1042|3139->1074|3168->1075|3203->1083|3298->1151|3326->1152|3361->1160|3404->1175|3433->1176|3468->1184|3764->1453|3792->1454|3827->1462|3876->1483|3905->1484|3940->1492|3999->1524|4027->1525|4058->1529
                  LINES: 21->1|26->1|28->3|34->9|34->9|36->11|36->11|37->12|46->21|46->21|48->23|48->23|48->23|49->24|55->30|55->30|57->32|57->32|57->32|58->33|60->35|60->35|62->37|62->37|62->37|63->38|65->40|65->40|67->42|67->42|67->42|68->43|70->45|70->45|72->47|72->47|72->47|73->48|74->49|74->49|76->51|76->51|76->51|77->52|80->55|80->55|82->57|82->57|82->57|83->58|86->61|86->61|88->63|88->63|88->63|89->64|99->74|99->74|101->76|101->76|101->76|102->77|103->78|103->78|104->79
                  -- GENERATED --
              */
          