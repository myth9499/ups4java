package cn.bankdev;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class InitTranMap {
	
	Hashtable<String, TranMap>	tmap=new	Hashtable<String, TranMap>();
	String	trancode;
	int	i=0;

	 File filepath = new File("cfg/tranmap/");
	public	InitTranMap(){
		String[]	filelist=filepath.list();
		for(int i=0;i<filelist.length;i++){
			File	file	=	new	File(filepath+"//"+filelist[i]);
			System.out.println("开始注册["+file.getName()+"]模块");
			tranmapinit(file);
		}
	}

	public Hashtable<String, TranMap> getTmap() {
		return tmap;
	}

	public void setTmap(Hashtable<String, TranMap> tmap) {
		this.tmap = tmap;
	}

	public	void	tranmapinit(File file2) {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file2);
			Element root = doc.getRootElement();//获取根节点
			System.out.println("root =	"+root.getName());
			List<Element> list=doc.selectNodes("TranMap/trancode");
			trancode = list.get(0).getText();
			List<Element> listip=doc.selectNodes("TranMap/trannode/destip");
			TranMap	tmpmap = new	TranMap();
			tmpmap.setTrancode(trancode);

			List<Element> listport=doc.selectNodes("TranMap/trannode/destport");

			List<Element> listtout=doc.selectNodes("TranMap/trannode/timeout");

			for(i=0;i<listip.size();i++)
			{
				tmpmap.InitTranMap(i,trancode,listip.get(i).getText(),Integer.parseInt(listport.get(i).getText()));
			}
			tmap.put(trancode, tmpmap);

		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("读入文件失败");
		}
	}

}
