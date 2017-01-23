package cn.hexing.fdm.services;

import java.io.IOException;

import cn.hexing.fdm.protocol.bll.dlmsService;
import cn.hexing.fdm.protocol.bll.dlmsService.DataType;
import cn.hexing.fdm.protocol.comm.CommOpticalSerialPort;
import cn.hexing.fdm.protocol.dlms.HXHdlcDLMS;
import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.iprotocol.IProtocol;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;

public class CommServer implements IcommServer {

	IProtocol DLMSProtocol = new HXHdlcDLMS();

	@Override
	public ICommucation OpenDevice(CommPara cpara, ICommucation commDevice) {
		ICommucation DevOpen = null;
		try {
			boolean blOpen = false;
			blOpen = commDevice.OpenDevice(cpara);
			if (blOpen) {
				DevOpen = commDevice;
			}
		} catch (Exception e) {
		}
		return DevOpen;
	}

	@Override
	public boolean Close(ICommucation commDevice) {
		boolean blClose = false;
		try {
			blClose = commDevice.Close();
		} catch (Exception e) {
		}
		return blClose;
	}

	public static byte[] stringArrayToByteArray(String[] strAryHex, int nLen) {
		if (strAryHex == null)
			return null;

		if (strAryHex.length < nLen) {
			nLen = strAryHex.length;
		}

		byte[] btAryHex = new byte[nLen];

		try {
			for (int i = 0; i < nLen; i++) {
				btAryHex[i] = (byte) Integer.parseInt(strAryHex[i], 16);
			}
		} catch (NumberFormatException e) {

		}

		return btAryHex;
	}

	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	public String strTakeNum(String str) {
		str = str.trim();
		String str2 = "";
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
					str2 += str.charAt(i);
				}
			}
		}
		return str2;
	}

	public  byte[] hexStr2Bytes(String src) {
	
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
		}
		return ret;
	}

	public byte[] GetCommuniteMeterAddr(String MeterAddr) {
		// string strData="";
		// if (MeterAddr.Length < 12) strData = MeterAddr.PadLeft(12, '0');
		// else strData = MeterAddr;
		// strData = strData.Substring(MeterNoCopyStart, MeterNoCopylen);
		// strData = strData.PadLeft(10, '0');

		// 当表号中数字超过9位时，只取表号的后9位（自动补0）。
		byte[] btyResult = null;
		String strData = "";
		try {
			MeterAddr = strTakeNum(MeterAddr);
			if (MeterAddr.length() < 9) {
				strData = String
						.format("%08x", Integer.parseInt(MeterAddr, 10));
				btyResult = hexStr2Bytes(strData);
			} else {
				strData = MeterAddr.substring(MeterAddr.length() - 9,
						MeterAddr.length());

			}

			strData = String.format("%08x", Integer.parseInt(strData, 10));

			btyResult = hexStr2Bytes(strData);
		} catch (Exception ex) {
		}
		return btyResult;
	}
	
	
	char HexChar(char c) {
		if ((c >= '0') && (c <= '9'))
			return (char) (c - 0x30);
		else if ((c >= 'A') && (c <= 'F'))
			return (char) (c - 'A' + 10);
		else if ((c >= 'a') && (c <= 'f'))
			return (char) (c - 'a' + 10);
		else
			return 0x10;
	}
	
	byte[] Str2Hex(String str) {
		int t, t1;
		int rlen = 0, len = str.length();
		final byte[] byteArray = new byte[str.length()];
		// data.SetSize(len/2);
		for (int i = 0; i < len;) {
			char l, h = str.charAt(i);
			if (h == ' ') {
				i++;
				continue;
			}
			i++;
			if (i >= len)
				break;
			l = str.charAt(i);
			t = HexChar(h);
			t1 = HexChar(l);
			if ((t == 16) || (t1 == 16))
				break;
			else
				t = t * 16 + t1;
			i++;
			byteArray[rlen] = (byte) t;
			rlen++;
		}

		final byte[] byteArray1 = new byte[rlen];
		System.arraycopy(byteArray, 0, byteArray1, 0, rlen);

		return byteArray1;

	}

	private DataType turnType(String strType) {
		DataType dtResult = null;
		if(strType.equals("Octs_ascii")) {
			dtResult = DataType.Octs_ascii;
		} else if(strType.equals("Octs_datetime")) {
			dtResult = DataType.Octs_datetime;
		} else if(strType.equals("Octs_hex")) {
			dtResult = DataType.Octs_hex;
		} else if(strType.equals("U8")) {
			dtResult = DataType.U8;
		} else if(strType.equals("U8_hex")) {
			dtResult = DataType.U8_hex;
		} else if(strType.equals("U16")) {
			dtResult = DataType.U16;
		} else if(strType.equals("U32")) {
			dtResult = DataType.U32;
		} else if(strType.equals("U32_hex")) {
			dtResult = DataType.U32_hex;
		} else if(strType.equals("Ascs")) {
			dtResult = DataType.Ascs;
		} else if(strType.equals("Octs_string")) {
			dtResult = DataType.Octs_string;
		}
		else if(strType.equals("Array_dd")) {
			dtResult = DataType.Array_dd;
		}
		else if(strType.equals("Bool")) {
			dtResult = DataType.bool;
		}

		return dtResult;
	}




	@Override
	public String Read(HXFramePara paraModel, ICommucation commDevice) {
		String strValue = null;
		try {

			//转换数据类型
			paraModel.decDataType = this.turnType(paraModel.strDecDataType);
			paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
			paraModel.sysTitleC = Str2Hex(paraModel.StrsysTitleC);
			if (paraModel.CommDeviceType == "Optical") {
				byte[] bDestAddr = { 0x03 };
				paraModel.DestAddr = bDestAddr;
			} else if (paraModel.CommDeviceType == "RF") {
				paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);

			}
			byte[] recbyt = DLMSProtocol.Read(paraModel, commDevice);
			strValue = dlmsService.TranXADRCode(recbyt, paraModel.decDataType);
		} catch (Exception e) {

		}
		return strValue;
	}

	@Override
	public boolean Write(HXFramePara paraModel, ICommucation commDevice) {
		boolean blWrite = false;
		try {

			//转换数据类型
			paraModel.decDataType = this.turnType(paraModel.strDecDataType);
			paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
			if (paraModel.CommDeviceType == "Optical") {
				byte[] bDestAddr = { 0x03 };
				paraModel.DestAddr = bDestAddr;
			} else if (paraModel.CommDeviceType == "RF") {
				paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);

			}
			paraModel.WriteData = dlmsService.GetXADRCode(paraModel.WriteData,
					paraModel.decDataType);
			blWrite = DLMSProtocol.Write(paraModel, commDevice);
		} catch (Exception e) {

		}
		return blWrite;
	}

	@Override
	public boolean Action(HXFramePara paraModel, ICommucation commDevice) {
		boolean blAction = false;
		try {

			//转换数据类型
			paraModel.decDataType = this.turnType(paraModel.strDecDataType);
			paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
			if (paraModel.CommDeviceType == "Optical") {
				byte[] bDestAddr = { 0x03 };
				paraModel.DestAddr = bDestAddr;
			} else if (paraModel.CommDeviceType == "RF") {
				paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);

			}
			blAction = DLMSProtocol.Action(paraModel, commDevice);
		} catch (Exception e) {

		}
		return blAction;
	}

	/***
	 * 断开通讯
	 * 
	 * @param commDevice
	 * @return
	 */
	public boolean DiscFrame(ICommucation commDevice) throws IOException {
		boolean blDisc = false;
		DLMSProtocol.DiscFrame(commDevice);
		return blDisc;
	}

	/***
	 * 测试OpenDevice
	 * 
	 * @param cpara
	 * @param commDevice
	 * @return
	 */
	public ICommucation OpenDeviceTest(CommPara cpara, ICommucation commDevice) {
		ICommucation DevOpen = null;
		return DevOpen;
	}

	/***
	 * 测试Close
	 * 
	 * @param commDevice
	 * @return
	 */
	public boolean CloseTest(ICommucation commDevice) {
		boolean blClose = false;
		return blClose;
	}

	/***
	 * 测试Read
	 * 
	 * @param paraModel
	 * @param commDevice
	 * @return
	 */
	public String ReadTest(HXFramePara paraModel, ICommucation commDevice) {
		return "1000Kw";
	}

	/***
	 * 测试Write
	 * 
	 * @param paraModel
	 * @param commDevice
	 * @return
	 */
	public boolean WriteTest(HXFramePara paraModel, ICommucation commDevice) {
		return true;
	}

	/***
	 * 测试Action
	 * 
	 * @param paraModel
	 * @param commDevice
	 * @return
	 */
	public boolean ActionTest(HXFramePara paraModel, ICommucation commDevice) {
		return true;
	}

	/***
	 * 测试DiscFrame
	 * 
	 * @param commDevice
	 * @return
	 */
	public boolean DiscFrameTest(ICommucation commDevice) {
		return true;

	}

}
