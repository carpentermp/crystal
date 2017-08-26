package au.id.bjf.dlx;

@SuppressWarnings("WeakerAccess")
public class SimpleDLXResultProcessor implements DLXResultProcessor {

	public boolean processResult(DLXResult result) {
		System.out.println(result.toString());
		return true;
	}

}
