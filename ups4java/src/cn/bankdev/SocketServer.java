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
	private	static	final	int	PORT=9998;
	private	static	int loop=0;
	private	static	List<Socket> list = new ArrayList<Socket>();
	private	ExecutorService	exec;
	private	ServerSocket	server;
	static Hashtable<String,TranMap[]>	tmap=	new	Hashtable<String,TranMap[]>();
	public	static	void	main(String[]	args){
		TranMap	tm[]	=	new	TranMap[10];
		tm[0]=new TranMap(0,"IXO102","10.0.137.17",7012);
		tm[1]= new TranMap(1,"IXO102","10.0.135.49",7013);
		tmap.put("IXO102", tm);
		new	SocketServer("netbank");
	}
	public	SocketServer(String chnlname){
		try{
			/** 获取该渠道对应监听端口 **/
			ChnlReg	cr	=	new  ChnlReg();
			cr.setChnlname(chnlname);
			server	=	new	ServerSocket(cr.getPort());
			exec	=	Executors.newCachedThreadPool();
			System.out.println("服务器已经启动!");
			logger.info("渠道:["+cr.getChnlmark()+"]"+"服务器已经启动!");
			Socket	client	=	null;
			while(true){
				client	=	server.accept();
				list.add(client);
				exec.execute(new ServerTask(client));
			}
		}catch(IOException e){
			logger.error(e.getMessage(), e);;
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
			//msg	= "["+this.socket.getInetAddress()+"]进去聊天室"+"当前聊天室有["+list.size()+"]人";
			//sendMessage();
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
		logger.info("传入信息:"+msg);
		/**
		for(Socket client:list){
			pw	=	new	PrintWriter(client.getOutputStream(),true);
			pw.println(msg);
			**/
		/** 获取配置进行转发 **/
		logger.info("开始将信息转发到["+tmap.get("IXO102")[loop%2].getIpport(loop%2).getServip()+"端口："+tmap.get("IXO102")[loop%2].getIpport(loop%2).getPort());
		try{
			clisocket	=	new	Socket(tmap.get("IXO102")[loop%2].getIpport(loop%2).getServip(),tmap.get("IXO102")[loop%2].getIpport(loop%2).getPort());
		}catch(IOException	e1)
		{
			pw	=	new	PrintWriter(socket.getOutputStream(),true);
			pw.println("连接失败!");
			logger.error("连接失败!");
			pw.close();
			socket.close();
			return ;
		}
		pw	=	new	PrintWriter(clisocket.getOutputStream(),true);
		pw.println(msg);
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