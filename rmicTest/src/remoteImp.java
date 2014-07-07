import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;


public class remoteImp extends UnicastRemoteObject implements myRemote{
	public remoteImp() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public String sayHello()
	{
		return "hello world!It's information from local!";
	}
	
	public static void main(String[] args)
	{
		
		try {
			 LocateRegistry.createRegistry(1099);
			myRemote service = new remoteImp();
			Naming.rebind("rmi://localhost:1099/remoteHello", service);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
