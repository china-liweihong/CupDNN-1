package cpudnn.optimizer;
import java.util.List;

import cpudnn.data.Blob;

public abstract class Optimizer {
	protected float lr = 0.0f;
	protected float lamda = 0.0f;
	public static enum GMode{
		NONE,
		L1,
		L2
	}
	GMode mode;
	public Optimizer(float lr){
		this.lr = lr;
		this.mode = GMode.NONE;
	}
	
	
	public Optimizer(float lr,GMode mode,float lamda){
		this.lr = lr;
		this.lamda = lamda;
		this.mode = mode;
	}
	public abstract void updateW(Blob params,Blob gradient);
	public abstract void updateB(Blob params,Blob gradient);
	public void setLr(float lr){
		this.lr = lr;
	}
	public float getLr(){
		return this.lr;
	}

}
