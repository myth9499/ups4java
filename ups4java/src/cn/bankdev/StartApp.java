package cn.bankdev;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bankdev.SocketServer.ServerTask;

public class StartApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/** 循环启动不同渠道的监听 **/
		List<String> allchnl = new	ArrayList<String>();
		ChnlReg	chnlreg	=	new	ChnlReg();
		allchnl = chnlreg.getallchnl();
		
		ExecutorService exec	=	Executors.newCachedThreadPool();
		
		System.out.println("allchnl size is "+allchnl.size());
		for(int i=0;i<allchnl.size();i++){
			System.out.println("开始启动第["+i+"]个渠道");
			exec.execute(new SocketServerMain(allchnl.get(i)));
		}
	}
	static	class	SocketServerMain implements	Runnable {
		String othargs	;
		public	SocketServerMain(String	chnlname){
			othargs=chnlname;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			new	SocketServer(othargs);
		}
		
	}

}
