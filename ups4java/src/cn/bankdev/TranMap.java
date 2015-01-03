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

/** 交易码与服务器主机之间映射**/
public class TranMap {

	private	String	trancode;
	private	String	trandes;
	private	Ipportmap[]	ipport=new	Ipportmap[10];//初始化10个数组
	
	public String getTrancode() {
		return trancode;
	}
	public void setTrancode(String trancode) {
		this.trancode = trancode;
	}
	public Ipportmap getIpport(int i) {
		return ipport[i];
	}
	public	int	getIpportCnt(){
		return	this.ipport.length;
	}
	public	void InitTranMap(int i,String	trancode,String	servip,int	port){
			if(i>10)
			{
				System.out.println("OUT OF RANGE");
				return	;
			}
			this.ipport[i]=new	Ipportmap();
			setTrancode(trancode);
			this.ipport[i].setServip(servip);
			this.ipport[i].setPort(port);
	}
	
}
