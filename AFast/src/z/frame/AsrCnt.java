package z.frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.i51talk.asr.AsrUtil;
import org.i51talk.asr.DecoderEx;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import z.util.AssetUtil;
import com.talk51.afast.log.Logger;

public class AsrCnt implements Handler.Callback,ICommon {
	public static class OnAsr implements DecoderEx.onAsr {
		@Override
		public void onResult(String res, boolean last) { }
		@Override
		public void onSpeech(boolean inSpeech) { }
		@Override
		public void onStart() { }
		@Override
		public void onVolume(double value) { }
		@Override
		public void onStop(int ms, double total, String[] words, int[] scores) { }
		@Override
		public void onTimer(int ms) { }
	}
	private DecoderEx mAsr = null;
	private DecoderEx.onAsr mCallBack = null;
	private Handler mUI = new Handler(Looper.getMainLooper(),this);
	private static final int CB_onResult = 801;
	private static final int CB_onSpeech = 802;
	private static final int CB_onStart = 803;
	private static final int CB_onVolume = 804;
	private static final int CB_onStop = 805;
	private static final int CB_onTimer = 806;
	@Override
	public boolean handleMessage(Message msg) {
		if (mCallBack==null) return true;
		switch (msg.what) {
		case CB_onResult:
			mCallBack.onResult((String)msg.obj, msg.arg1!=0);
			break;
		case CB_onSpeech:
			mCallBack.onSpeech(msg.arg1!=0);
			break;
		case CB_onStart:
			mCallBack.onStart();
			break;
		case CB_onVolume:
			mCallBack.onVolume((Double)msg.obj);
			break;
		case CB_onStop: {
			Object[] args = (Object[])msg.obj;
			String tr = (String)args[2];
			if (tr!=null) {
				// umeng统计dic缺失错误
//				UmengErr.reportWarning("asr dic words missing:\r\n"+tr);
				if (app.isDebug&&msg.arg1<=0) {
					Toast.makeText(app.ctx, tr, Toast.LENGTH_LONG).show();
				}
			}
			DecoderEx.onAsr cb = mCallBack;
			mCallBack = null;// 只执行一次就释放
			cb.onStop(msg.arg1, msg.arg2 * 0.0001, (String[])args[0], (int[])args[1]);
			break; }
		case CB_onTimer:
			mCallBack.onTimer(msg.arg1);
			break;
		}
		return true;
	}
	private DecoderEx.onAsr mProxy = new DecoderEx.onAsr() {
		@Override
		public void onResult(final String res, final boolean last) {
			if (mCallBack!=null) {
				mUI.obtainMessage(CB_onResult,last?1:0,0,res).sendToTarget();
			}
		}
		@Override
		public void onSpeech(final boolean inSpeech) {
			if (mCallBack!=null) {
				mUI.obtainMessage(CB_onSpeech,inSpeech?1:0).sendToTarget();
			}
		}
		@Override
		public void onStart() {
			if (mCallBack!=null) {
				mUI.obtainMessage(CB_onStart).sendToTarget();
			}
		}
		@Override
		public void onVolume(final double value) {
			if (mCallBack!=null) {
				mUI.obtainMessage(CB_onVolume,(Double)value).sendToTarget();
			}
		}
		@Override
		public void onStop(final int ms, final double total, final String[] words, final int[] scores) {
			if (mCallBack!=null) {
				Object[] args = new Object[] {words,scores, mAsr.getTrace()};
				mUI.obtainMessage(CB_onStop,ms,(int)(total*10000),args).sendToTarget();
			}
		}
		@Override
		public void onTimer(final int ms) {
			if (mCallBack!=null) {
				mUI.obtainMessage(CB_onTimer,ms,0).sendToTarget();
			}
		}
	};
	
	private static final String ASR_DIR = "asr";
	private static AsrCnt s_instance = null;
	private static File getRootDir() { // 放到内部存储
		return new File(app.ctx.getFilesDir(),ASR_DIR);
//		return new File(app.ctx.getExternalFilesDir(null),ASR_DIR);
	}
	private AsrCnt() {
		File rootDir = getRootDir();
		mAsr = new DecoderEx.Asr()
		.setCfgDir(new File(rootDir,"model_en").getAbsolutePath())
		.setDic(new File(rootDir,"my.dic").getAbsolutePath())
		.setNoiseLevel(app.mNoiseLevel)
		.setGramFile(new File(rootDir,"my.gram").getAbsolutePath())
		.build();
		mAsr.setCallback(mProxy);
		mAsr.getHdler().post(new Runnable() {
			@Override
			public void run() {
				File rootDir = getRootDir();
				AssetUtil.copyAssetFiles("asr", rootDir); // 拷贝默认词典 以后要删除
				rootDir = new File(rootDir,"model_en");
				boolean needCopy = !rootDir.exists();
				if (!needCopy) {
					String[] FileList = rootDir.list();
					needCopy = (FileList==null)||(FileList.length < 6);
				}
				if (needCopy) {
					boolean isSuccess = AssetUtil.copyAssetFiles("asr/model_en", rootDir);
					if(isSuccess){
						Logger.i("AsrCnt", "初始化评分数据成功 >>>");
					}else{
						Logger.i("AsrCnt", "初始化评分数据失败 >>>");
					}
				}else{
					Logger.i("AsrCnt", "评分数据已经存在，无需初始化");
				}
			}
		});
		mAsr.enableTrace(app.isDebug);
	}
	public static AsrCnt getInstance() {
		if (s_instance==null) {
			s_instance = new AsrCnt();
		}
		return s_instance;
	}
	public static void startNonAsr(String record,int sec,int timerMs,OnAsr cb) {
		AsrCnt ac =  getInstance();
		ac.mCallBack = cb;
		ac.mAsr.setAacPath(record);
		ac.mAsr.enableAsr(false);
		ac.mAsr.start(sec, timerMs);	
	}
	public static void startAsr(String[] names,String[] words,OnAsr cb,String record,int sec,int timerMs) {
		AsrCnt ac =  getInstance();
		ac.mAsr.initInfo(names, words);
		ac.mCallBack = cb;
		ac.mAsr.setAacPath(record);
		ac.mAsr.enableAsr(true);
		ac.mAsr.start(sec, timerMs);
	}
	public static void stopAsr() {
		AsrCnt ac = s_instance;
		if (ac!=null) {
			ac.mAsr.stop();
		}
	}
	public static void cancelAsr() {
		AsrCnt ac = s_instance;
		if (ac!=null) {
			ac.mCallBack = null;
			ac.mAsr.stop();
		}
	}
	public static void destroy() {
		AsrCnt ac = s_instance;
		if (ac!=null) {
			s_instance = null;
			ac.mCallBack = null;
			ac.mAsr.stop();
			ac.mAsr.destroy();
			ac.mAsr = null;
		}		
	}
	// 在非ui线程执行
	public static void postNonUI(Runnable r) {
		AsrCnt ac =  getInstance();	
		ac.mAsr.getHdler().post(r);
	}
	// 检查录音权限
	public static boolean checkRecordPermission() {
		return AsrUtil.createRecord(0)!=null;
	}
	// 拷贝文件
	public static boolean copyFile(String src,String dst) {
		FileInputStream is = null;
		FileOutputStream os = null;
		boolean res = true;
		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(dst);
			byte[] buf = new byte[4096];
			int len = 0;
			while ((len=is.read(buf))>0) {
				os.write(buf, 0, len);
			}
			is.close();
			is = null;
			os.close();
			os = null;
		} catch (Exception e) {
			e.printStackTrace();
			res = false;
			try {
				if (is!=null) {
					is.close();
					is = null;
				}
				if (os!=null) {
					os.close();
					os = null;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return res;
	}
	// 设置字典
	public boolean setDic(String dicFile) {
		if (copyFile(dicFile, new File(getRootDir(), "my.dic").getAbsolutePath())) {
			mAsr.getCfg().setReloadDic(true);
			return true;
		}
		return false;
	}
	public void enableTrace(boolean bOn) {
		AsrCnt ac =  getInstance();
		if (ac==null) return;
		ac.mAsr.enableTrace(bOn);
	}
	public static String getTrace() {
		AsrCnt ac =  getInstance();
		return ac!=null?ac.mAsr.getTrace():null;
	}
}
