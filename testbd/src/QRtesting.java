
import java.util.List;

import org.dcm4che2.data.*;
import org.dcm4che2.tool.dcmqr.*;


public class QRtesting {
	
	public static void main(String[] args) throws Exception{
		
		DcmQR dcmqr = new DcmQR("ddnet");

		dcmqr.setCalledAET("DCM4CHEE", true);
		dcmqr.setRemoteHost("127.0.0.1");
		dcmqr.setRemotePort(11112);

		//dcmqr.setCFind(true);
        dcmqr.setCGet(true);

		dcmqr.configureTransferCapability(true);

        dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.IMAGE);
        

		dcmqr.addMatchingKey(new int[]{Tag.Modality}, "XA");

		dcmqr.addReturnKey(new int[]{Tag.PatientName});
		dcmqr.addReturnKey(new int[]{Tag.NumberOfFrames});
		dcmqr.addReturnKey(new int[]{Tag.CineRate});
		dcmqr.addReturnKey(new int[]{Tag.FrameTime});
		dcmqr.addReturnKey(new int[]{Tag.FrameIncrementPointer});
		dcmqr.addReturnKey(new int[]{Tag.FrameDelay});
		dcmqr.addReturnKey(new int[]{Tag.RecommendedDisplayFrameRate});
		

		List<DicomObject> result = null;

        dcmqr.start();
        dcmqr.open();
        result = dcmqr.query();
        result.add(dcmqr.getKeys());
        dcmqr.get(result);
        dcmqr.stop();
        dcmqr.close();
        
        for(DicomObject dcmobj : result){
        	System.out.println("Paciente: "+dcmobj.getString(Tag.PatientName));
        	System.out.println("NumberOfFrames: "+dcmobj.getInt(Tag.NumberOfFrames));
        	System.out.println("CineRate: "+dcmobj.getInt(Tag.CineRate));
        	System.out.println("FrameTime: "+dcmobj.getFloat(Tag.FrameTime));
        	System.out.println("FrameIncrementPointer: "+dcmobj.getString(Tag.FrameIncrementPointer));
        	System.out.println("FrameDelay: "+dcmobj.getString(Tag.FrameDelay));
        	System.out.println("Recommended Display Frame Rate: "+dcmobj.getString(Tag.RecommendedDisplayFrameRate));
        	System.out.println("...................................");
        }
        
		
	}

}
