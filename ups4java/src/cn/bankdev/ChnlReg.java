package cn.bankdev;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

/** 通过解析chnlreg.xml配置获取到对应的配置信息 **/
public class ChnlReg {
	private	String	chnlname;
	private	String	chnlmark;
	private	int	port;
	
	private List <Element> allLeafs = new ArrayList<Element> ();
	File	file	=	new	File("/home/dev/git/ups4java/ups4java/cfg/chnlreg.xml");

	public String getChnlname() {
		return chnlname;
	}
	private List<Element> getAllLeafNode(File file2) {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file2);
			Element root = doc.getRootElement();//获取根节点
			System.out.println("root =	"+root.getName());
			Iterator <Element> allSons = root.elementIterator();
			while (allSons.hasNext()) {
				getLeafNodes(allSons.next());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("读入文件失败");
		}
		return allLeafs;
	}
	private void getLeafNodes(Element next) {
		// TODO Auto-generated method stub
		Element e = next;
		if (e.elements().size() > 0) {
			List <Element> el = e.elements();
			for (Element sonNode : el) {
				if (sonNode.elements().size() > 0) {
					getLeafNodes(sonNode);
				} else {
					if(el.get(0).getText().equals(this.getChnlname()))
						allLeafs.add(sonNode);
				}
			}
		}
	}
	public void setChnlname(String chnlname) {
		this.chnlname = chnlname;
	}
	public String getChnlmark() {
		return chnlmark;
	}
	public void setChnlmark(String chnlmark) {
		this.chnlmark = chnlmark;
	}
	public int getPort() {
		List <Element> list=this.getAllLeafNode(file);
		for (Element e : list) {
			System.out.println("e.name =	"+e.getName()+"	e.getText =	"+e.getTextTrim());
			this.setChnlmark(list.get(1).getText());
			this.setPort(Integer.parseInt(list.get(2).getText()));
		}
		return this.port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
