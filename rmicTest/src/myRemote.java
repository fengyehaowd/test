import java.rmi.Remote;
import java.rmi.RemoteException;


public interface myRemote extends Remote{
	public String sayHello() throws RemoteException;

}
