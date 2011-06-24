package edu.colorado.phet.ladybugmotion2d.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.view.util.PhetFont
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.{BasicStroke, Color}
import java.text.DecimalFormat
import edu.colorado.phet.scalacommon.Predef._
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.ladybugmotion2d.model.LadybugModel

class DigitalTimer(model: LadybugModel) extends PNode {
  val text = new PText("123.27 sec")
  text.setFont(new PhetFont(42))

  val background = new PhetPPath(text.getFullBounds, Color.lightGray, new BasicStroke(1), Color.darkGray)
  addChild(background)
  addChild(text)

  /*
    Non-Control Structure way of doing this, we are prone to forget:
     (a) to call the first update or (b) to attach listener to the model
     Using a control structure ensures everything will happen
  m.addListenerByName(update())
  update
  def update()= {
  text.setText(new DecimalFormat("0.00").format(m.getTime)+" sec")
   */

  val update = defineInvokeAndPass(model.addListenerByName) {
                                                              text.setText(new DecimalFormat("0.00").format(model.getTime) + " sec")
                                                            }

}