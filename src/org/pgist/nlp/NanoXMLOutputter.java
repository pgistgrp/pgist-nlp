package org.pgist.nlp;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;


/**
 * Create Touchgraph xml file.
 * @author kenny
 *
 */
public class NanoXMLOutputter {

    
    private static String[] colors = {
        "606060",
        "0000E0",
        "A04000",
        "D00000",
        "FF00FF",
        "FF0000"
    };//colors
    
    
    public void output(Map map, List vertexList, String fileName) {
        //The top level element
        XMLElement tglbXML = new XMLElement("TOUCHGRAPH_LB");
        tglbXML.setAttribute("version", "1.20");
        
        //the node set
        XMLElement nodeSet = new XMLElement("NODESET");                
        tglbXML.addChild(nodeSet);
        
        //the edge set
        XMLElement edgeSet = new XMLElement("EDGESET");
        tglbXML.addChild(edgeSet);
        
        
        //create node (vertex) section
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            Word one = (Word) map.get(key);
            XMLElement nodeElt = createVertexNode(one);
            nodeSet.addChild(nodeElt);
        }//for iter
        
        
        //create edge section
        for (int i=0; i<vertexList.size(); i++) {
            List edgeList = (List) vertexList.get(i);
            Word first = (Word) edgeList.get(0);
            for (int j=1; j<edgeList.size(); j++) {
                Word one = (Word) edgeList.get(j);
                XMLElement nodeElt = createEdgeNode(first, one);
                edgeSet.addChild(nodeElt);
            }//for j
        }//for i
        
        
        //create parameter section
        XMLElement parameters = new XMLElement("PARAMETERS");
        tglbXML.addChild(parameters);
        XMLElement param = new XMLElement("PARAM");
        param.setAttribute("name", "offsetX");
        param.setAttribute("value", "0");
        parameters.addChild(param);
        param = new XMLElement("PARAM");
        param.setAttribute("name", "offsetY");
        param.setAttribute("value", "0");
        parameters.addChild(param);
        param = new XMLElement("PARAM");
        param.setAttribute("name", "rotateSB");
        param.setAttribute("value", "0");
        parameters.addChild(param);
        param = new XMLElement("PARAM");
        param.setAttribute("name", "zoomSB");
        param.setAttribute("value", "0");
        parameters.addChild(param);
        
        
        //output to file
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
            
            //dtd section
            InputStream prependDTD = getClass().getResourceAsStream("/TG_Prepend_DTD.xml");
            int c; //Slow, but gets the job done for small files
            while((c = prependDTD.read())!=-1) {
                out.write(c);
            }
            prependDTD.close();
            
            XMLWriter writer = new XMLWriter(out);
            writer.write(tglbXML, true);
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    private XMLElement createEdgeNode(Word from, Word to) {
        XMLElement edgeElt = new XMLElement("EDGE");                
        edgeElt.setAttribute("fromID", from.getName());
        edgeElt.setAttribute("toID", to.getName());
        edgeElt.setAttribute("type", "1");
        //edgeElt.setAttribute("length", "10");                              
        edgeElt.setAttribute("visible", "true");
        edgeElt.setAttribute("color", encodeColor(Color.red));                
        return edgeElt;
    }


    private XMLElement createVertexNode(Word word) {
        XMLElement nodeElt = new XMLElement("NODE");
        nodeElt.setAttribute("nodeID", word.getName());
        
        XMLElement nodeLocation = new XMLElement("NODE_LOCATION");
        nodeLocation.setAttribute("x", "0");
        nodeLocation.setAttribute("y", "0");
        nodeLocation.setAttribute("visible", "true");
        nodeElt.addChild(nodeLocation);
        
        XMLElement nodeLabel = new XMLElement("NODE_LABEL");
        nodeLabel.setAttribute("label", word.getName());
        nodeLabel.setAttribute("shape", "3");
        nodeLabel.setAttribute("backColor", colors[word.getLevel()]);
        nodeLabel.setAttribute("textColor", encodeColor(Color.black));
        nodeLabel.setAttribute("fontSize", "16");
        nodeElt.addChild(nodeLabel);
        
        /*
        XMLElement nodeURL = new XMLElement("NODE_URL");
        nodeURL.setAttribute("url", lbNode.getURL());
        nodeURL.setAttribute("urlIsLocal", lbNode.getURLIsLocal() ? "true" : "false");
        nodeURL.setAttribute("urlIsXML", lbNode.getURLIsXML() ? "true" : "false");
        nodeElt.addChild(nodeURL);
        
        XMLElement nodeHint = new XMLElement("NODE_HINT");
        nodeHint.setAttribute("hint", lbNode.getHint());
        nodeHint.setAttribute("width", ""+lbNode.getHintWidth());
        nodeHint.setAttribute("height", ""+lbNode.getHintHeight());
        nodeHint.setAttribute("isHTML", lbNode.getHintIsHTML() ? "true" : "false");
        nodeElt.addChild(nodeHint);
        */
        
        return nodeElt;
    }


    private String encodeColor(Color c) {
        if (c == null) return null;        
        int rgb = c.getRGB()&0xffffff;
        String zeros = "000000";
        String data = Integer.toHexString(rgb);
        return (zeros.substring(data.length()) + data).toUpperCase();
    }


}
