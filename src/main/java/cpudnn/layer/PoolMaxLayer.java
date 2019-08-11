package cpudnn.layer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import cpudnn.Network.Network;
import cpudnn.data.Blob;
import cpudnn.util.Task;
import cpudnn.util.ThreadPoolManager;

public class PoolMaxLayer extends Layer{
	public static final String TYPE = "PoolMaxLayer";
	
	private Blob maxIndex;
	private int width;
	private int height;
	private int inChannel;
	private int kernelSize;
	private int stride;
	
	public PoolMaxLayer(Network network){
		super(network);
	}
	
	public PoolMaxLayer(Network network,int width,int height,int inChannel,int kernelSize,int stride) {
		// TODO Auto-generated constructor stub
		super(network);
		this.width = width;
		this.height = height;
		this.inChannel = inChannel;
		this.kernelSize = kernelSize;
		this.stride = stride;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		maxIndex = new Blob(mNetwork.getBatch(),inChannel,height,width);

	}

	@Override
	public void forward() {
		// TODO Auto-generated method stub
		
		Blob input = mNetwork.getDatas().get(id-1);
		Blob output = mNetwork.getDatas().get(id);
		float [] outputData = output.getData();
		float [] inputData = input.getData();
		float [] maxIndexData = maxIndex.getData();
		Vector<Task<Object>> workers = new Vector<Task<Object>>();
		for(int n=0;n<output.getNumbers();n++){
			workers.add(new Task<Object>(n) {
				@Override
			    public Object call() throws Exception {
					for(int c=0;c<output.getChannels();c++){
						for(int h=0;h<output.getHeight();h++){
							for(int w=0;w<output.getWidth();w++){
								int inStartX = w*stride;
								int inStartY = h*stride;
								float localMaxVlue = 0;
								int localMaxIndex = 0;
								for(int kh=0;kh<kernelSize;kh++){
									for(int kw=0;kw<kernelSize;kw++){
										int curIndex = input.getIndexByParams(n, c, inStartY+kh, inStartX+kw);
										if(inputData[curIndex]>localMaxVlue){
											localMaxVlue = inputData[curIndex];
											localMaxIndex = kh*kernelSize+kw;
										}
									}
								}
								maxIndexData[maxIndex.getIndexByParams(n, c, h, w)] = localMaxIndex;
								outputData[output.getIndexByParams(n, c, h, w)] = localMaxVlue;
							}
						}
					}
					return null;
				}
			});
		}	
		ThreadPoolManager.getInstance(mNetwork).dispatchTask(workers);
	}

	@Override
	public void backward() {
		// TODO Auto-generated method stub
		Blob inputDiff = mNetwork.getDiffs().get(id);
		Blob outputDiff = mNetwork.getDiffs().get(id-1);
		float[] inputDiffData = inputDiff.getData();
		float[] outputDiffData = outputDiff.getData();
		float [] maxIndexData = maxIndex.getData();
		
		Vector<Task<Object>> workers = new Vector<Task<Object>>();
		for(int n=0;n<inputDiff.getNumbers();n++){
			workers.add(new Task<Object>(n) {
				@Override
			    public Object call() throws Exception {
					for(int c=0;c<inputDiff.getChannels();c++){
						for(int h=0;h<inputDiff.getHeight();h++){
							for(int w=0;w<inputDiff.getWidth();w++){
								int inStartX = w*stride;
								int inStartY = h*kernelSize;
								int iY = (int)maxIndexData[maxIndex.getIndexByParams(n, c, h, w)]/kernelSize;
								int iX = (int)maxIndexData[maxIndex.getIndexByParams(n, c, h, w)]%kernelSize;

								outputDiffData[outputDiff.getIndexByParams(n, c, inStartY+iY, inStartX+iX)] = inputDiffData[inputDiff.getIndexByParams(n, c, h, w)];
							}
						}
					}
					return null;
				}
			});
		}	
		ThreadPoolManager.getInstance(mNetwork).dispatchTask(workers);
	}

	@Override
	public void saveModel(ObjectOutputStream out) {
		// TODO Auto-generated method stub
		try {
			out.writeUTF(getType());
		    out.writeInt(width);
		    out.writeInt(height);
		    out.writeInt(inChannel);
		    out.writeInt(kernelSize);
			out.writeInt(stride);
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
			inChannel = in.readInt();
			kernelSize = in.readInt();
			stride = in.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Blob createOutBlob() {
		// TODO Auto-generated method stub
		return new Blob(mNetwork.getBatch(),inChannel,width/2,height/2);
	}

	@Override
	public Blob createDiffBlob() {
		// TODO Auto-generated method stub
		return new Blob(mNetwork.getBatch(),inChannel,width/2,height/2);
	}

}
