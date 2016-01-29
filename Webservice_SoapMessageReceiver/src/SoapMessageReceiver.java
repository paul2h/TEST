import java.io.StringWriter;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**
 * 從webservice 取得SOAP的基本接收
 * 
 * <pre>
 * 
 * 接收步驟:<br>
 * 1.先用soapUI確認webservice正常運作<br>
 * 2.利用以下方式取得資料
 * */
public class SoapMessageReceiver {
	
	SOAPConnectionFactory soapConnFactory;
	SOAPConnection connection;
	SOAPMessage SOAPrequest;// 傳出去的request
	SOAPMessage SOAPrespond;// 接收到的資料respond
	String respondString;// 從respond轉換過後的String

	/** 實際測試的main */
	public static void main(String[] args) {
		SoapMessageReceiver rsm = new SoapMessageReceiver();
		System.out.println(rsm.getSOAPData());
	}

	public String getSOAPData() {
		try {
			
			creatSOAPConnection();
			creatSOAPMessage();
			editSOAPMessage();
			sendSOAPMessage();
			analysisRespond();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		/** 沒有成功接收到資料 */
		if (respondString == null) {
			respondString = "接收未成功";
		}
		return respondString;
	}

	/**
	 * 創建SOAP連接
	 * 
	 * @throws SOAPException
	 * @throws UnsupportedOperationException
	 */
	private void creatSOAPConnection() throws UnsupportedOperationException, SOAPException {
		// 第一次創建連接
		if (soapConnFactory == null) {
			soapConnFactory = SOAPConnectionFactory.newInstance();
			connection = soapConnFactory.createConnection();
		}
	}

	/**
	 * 創建要送出的SOAP訊息
	 * 
	 * @throws SOAPException
	 */
	private void creatSOAPMessage() throws SOAPException {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPrequest = messageFactory.createMessage();
	}

	/**
	 * 編寫要送出的SOAP訊息
	 * 
	 * @throws SOAPException
	 */
	private void editSOAPMessage() throws SOAPException {
		/* 取得soap物件的body部分 */
		SOAPPart soapPart = SOAPrequest.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
		/* 設定namespace */
		envelope.setAttribute("xmlns:tep", "http://tepmuri.org");
		SOAPBody body = envelope.getBody();
		/* request的參數放入body中 */
		@SuppressWarnings("unused")
		SOAPElement bodyElement = body.addChildElement("tep:RegionWaterStations");
		/* 儲存變更 */
		SOAPrequest.saveChanges();
	}

	/**
	 * 發送SOAP訊息 並取得回傳資訊
	 * 
	 * @throws SOAPException
	 */
	private void sendSOAPMessage() throws SOAPException {
		/* 設定要連到的webservice URL (此URL可從該webservice的wsdl文件中獲得) */
		URLEndpoint urlEndpoint = new URLEndpoint("http://118.163.44.187/Yilan/ws/WaterLevelService");
		/* 發送訊息 */
		SOAPrespond = connection.call(SOAPrequest, urlEndpoint);
	}

	/**
	 * 分析回傳回來的SOAP物件
	 * 
	 * @throws SOAPException
	 * @throws TransformerException
	 */
	private void analysisRespond() throws SOAPException, TransformerException {
		/* soap轉換 成String */
		// 確認轉換物件存在
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		// 抓取SOAP的內容
		Source sourceContent = SOAPrespond.getSOAPPart().getContent();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(sourceContent, result);
		respondString = writer.toString();
	}
}