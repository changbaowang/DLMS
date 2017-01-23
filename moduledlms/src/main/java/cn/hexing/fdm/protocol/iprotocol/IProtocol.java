package cn.hexing.fdm.protocol.iprotocol;

import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.HXFramePara;

public interface IProtocol {

	/***
	 * 读取
	 * @param paraModel
	 * @param commDevice
	 * @return
	 */
	 byte[] Read(HXFramePara paraModel, ICommucation commDevice);
	
	 /***
	  * 设置
	  * @param paraModel
	  * @param commDevice
	  * @return
	  */
	 boolean Write(HXFramePara paraModel, ICommucation commDevice);
	
	 /***
	 * 执行
	 * @param paraModel
	 * @param commDevice
	 * @return
	 */
	 boolean Action(HXFramePara paraModel, ICommucation commDevice);
   
	 /***
     * 断开链路
     * @param commDevice
     * @return
     */
	 boolean DiscFrame(ICommucation commDevice); 
}
