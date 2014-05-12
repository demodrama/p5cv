
public class KModel {
	// Mode
	
	public float xk;
	public float xk_1;
	public float uk;
	public float wk_1;
	public float zk;
	public float vk;

	public float A = 1;
	public float B = 0.1f;
	public float H = 1;
	
	
	public void update() {
		// ruido estocástico
		uk = (float) (0.1f*(Math.random()-0.5));
		
		xk = A * xk_1 + B * uk + wk_1;
		zk = H * xk + vk;
		xk_1 = xk;		
	}
	
}
