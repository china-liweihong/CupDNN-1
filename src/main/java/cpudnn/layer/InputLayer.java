package cpudnn.layer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cpudnn.Network.Network;
import cpudnn.data.Blob;
/*
 * InputLayer��Ҫ������ռ�ݵ�һ��λ�ã��ǵķ��򴫲����㷨������ʵ��
 */
import cpudnn.data.BlobParams;

public class InputLayer extends Layer{
	public static final String TYPE = "InputLayer";
	
	Network mNetwork;
	int width;
	int height;
	int channel;
	
	public InputLayer(Network network){
		super(network);
		this.mNetwork = network;
	}
	
	public InputLayer(Network network, int width,int height,int channel) {
		super(network);
		// TODO Auto-generated constructor stub
		this.width = width;
		this.height = height;
		this.channel = channel;
		this.mNetwork = network;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forward() {
		// TODO Auto-generated method stub
		//do nothing
	}

	@Override
	public void backward() {
		// TODO Auto-generated method stub
		//do nothing
	}
	
	public void setInputData(Blob input){
		Blob curData = mNetwork.getDatas().get(id);
		input.cloneTo(curData);
	}

	@Override
	public void saveModel(ObjectOutputStream out) {
		// TODO Auto-generated method stub
		try {
			out.writeUTF(getType());
			//�����ʱ��batchҲ����layerParams��number����1����Ϊpredict��ʱ����Ϊ����ʹ�õ�ʱ�����batchһ�㶼��1
			out.writeInt(width);
			out.writeInt(height);
			out.writeInt(channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void loadModel(ObjectInputStream in) {
		// TODO Auto-generated method stub
		try {
			width = in.readInt();
			height = in.readInt();
			channel = in.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Blob createOutBlob() {
		// TODO Auto-generated method stub
		return new Blob(mNetwork.getBatch(),channel,height,width);
	}

	@Override
	public Blob createDiffBlob() {
		// TODO Auto-generated method stub
		return new Blob(mNetwork.getBatch(),channel,height,width);
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getChannel() {
		return channel;
	}
}
