package cn.bankdev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;


public	class	SocketServer{

	static Logger logger = Logger.getLogger(SocketServer.class);
	private	static	int loop=0;//用来做负载分配使用，目前暂时使用轮询机制
	private	ExecutorService	exec;
	private	ServerSocket	server;
	static Hashtable<String,TranMap>	tmap=	new	Hashtable<String,TranMap>();

	public  SocketServer(String chnlname){
		InitTranMap itm = new	InitTranMap();
		tmap=itm.getTmap();
		try{
			/** 获取该渠道对应监听端口 **/
			ChnlReg	cr	=	new  ChnlReg();
			cr.setChnlname(chnlname);
			server	=	new	ServerSocket(cr.getPort());
			exec	=	Executors.newCachedThreadPool();
			logger.info("渠道:["+cr.getChnlmark()+"]"+"服务已经启动!监听端口:["+cr.getPort()+"]");
			Socket	client	=	null;
			while(true){
				client	=	server.accept();
				exec.execute(new ServerTask(client));
			}
		}catch(IOException e){
			logger.error("启动服务失败:"+e.getMessage(), e);
		}
	}
	static	class	ServerTask	implements	Runnable{
		private	Socket	socket,clisocket;
		private	int	remoteport;
		private	BufferedReader	br;
		private	PrintWriter	pw;
		private	String	msg;
		public	ServerTask(Socket	socket) throws IOException{
			this.socket=socket;
			logger.info("处理来自["+this.socket.getInetAddress()+"]"+"的交易信息");
			br	=	new	BufferedReader(new	InputStreamReader(socket.getInputStream()));
		}
		public	void	run(){
			try{
				msg=br.readLine();
				if(msg!=null){
					sendMessage();
				}else
				{
					logger.error("获取socket内容失败");
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	/** 转发报文到配置的IP地址和端口 **/
	private	void	sendMessage() throws IOException{
		logger.info("传入信息:["+msg+"]");
		String	[]	mess ;
		Messages	message=new	Messages();
		mess = msg.split("@");
		System.out.println(mess[0]+mess[1]+mess[2]);
		message.setTrancode(mess[0]);
		message.setMsglen(Integer.parseInt(mess[1]));
		message.setMessage(mess[2]);
		if(message.getMsglen()!=message.getMessage().length()){
			logger.error("报文长度不符"+"reallen"+message.getMessage().length());
			return ;
		}
		/** 需要进行报文解析 **/
		/**
		for(Socket client:list){
			pw	=	new	PrintWriter(client.getOutputStream(),true);
			pw.println(msg);
			**/
		/** 获取配置进行转发 **/
		int	cnt;
		if(tmap.get(message.getTrancode()) == null){
			logger.error("交易码:"+message.getTrancode()+"未配置！");
			pw	=	new	PrintWriter(socket.getOutputStream(),true);
			pw.println("交易码:"+message.getTrancode()+"未配置！");
			socket.close();
			return;
		}
		cnt	=	tmap.get(message.getTrancode()).getIpportCnt();
		cnt = 3;
		String	ipaddr=tmap.get(message.getTrancode()).getIpport(loop%cnt).getServip();
		int	port	=	tmap.get(message.getTrancode()).getIpport(loop%cnt).getPort();
		logger.info("开始将信息转发到["+ipaddr+"端口："+port);
		try{
			clisocket	=	new	Socket(ipaddr,port);
		}catch(IOException	e1)
		{
			pw	=	new	PrintWriter(socket.getOutputStream(),true);
			pw.println("连接失败!");
			logger.error("连接失败!");
			pw.close();
			socket.close();
			loop++;
			return ;
		}
		pw	=	new	PrintWriter(clisocket.getOutputStream(),true);
		//pw.println(msg);
		pw.println(message.getMessage());
		/** 接收其他系统返回信息，转发到连接系统 **/
		br	=	new	BufferedReader(new	InputStreamReader(clisocket.getInputStream()));
		String	othmsg;
		while((othmsg=br.readLine())!=null){
			logger.info("收到其他系统返回："+othmsg);
			/** 返回给客户端系统 **/
			pw	=	new	PrintWriter(socket.getOutputStream(),true);
			pw.println(othmsg);
		}
		br.close();
		pw.close();
		socket.close();
		clisocket.close();
		loop++;
		}
	}
	
}