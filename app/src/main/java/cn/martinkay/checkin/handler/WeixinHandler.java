package cn.martinkay.checkin.handler;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import cn.martinkay.checkin.handler.pageprocessor.weixin.CompleteProcessor;
import cn.martinkay.checkin.handler.pageprocessor.weixin.MessagePageProcessor;
import cn.martinkay.checkin.handler.pageprocessor.weixin.SiginInProcessor;
import cn.martinkay.checkin.handler.pageprocessor.weixin.WorkPageProcessor;
import cn.martinkay.checkin.service.MyAccessibilityService;
import cn.martinkay.checkin.util.AccessibilityHelper;
import cn.martinkay.checkin.utils.AutoSignPermissionUtils;

public class WeixinHandler implements BaseHandler {
    private static final String TAG = "WeixinHandler";
    public static final String packageName = "com.tencent.wework";
    private WorkPageProcessor workPageProcessor = new WorkPageProcessor();
    private MessagePageProcessor messagePageProcessor = new MessagePageProcessor();
    private SiginInProcessor siginInProcessor = new SiginInProcessor();
    private CompleteProcessor completeProcessor = new CompleteProcessor();

    // 添加延迟控制变量
    private long startTime = 0;

    @Override
    public void doHandle(AccessibilityEvent event, MyAccessibilityService myAccessibilityService)
            throws Exception {
        if (!AutoSignPermissionUtils.INSTANCE.isMobileAutoSignLaunch()) {
            return;
        }

        // 添加10秒延迟逻辑（仅需这3行代码）
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis() - startTime < 10000) {
            return;
        }

        AccessibilityNodeInfo nodeById = AccessibilityHelper.getNodeById(myAccessibilityService,
                "com.tencent.wework:id/hrb",
                0);
        if (nodeById != null) {
            AccessibilityHelper.clickButtonByNode(myAccessibilityService, nodeById);
        } else if (this.messagePageProcessor.canParse(event, myAccessibilityService)) {
            this.messagePageProcessor.processPage(event, myAccessibilityService);
        } else if (this.workPageProcessor.canParse(event, myAccessibilityService)) {
            this.workPageProcessor.processPage(event, myAccessibilityService);
        } else if (this.completeProcessor.canParse(event, myAccessibilityService)) {
            this.completeProcessor.processPage(event, myAccessibilityService);
        } else if (this.siginInProcessor.canParse(event, myAccessibilityService)) {
            this.siginInProcessor.processPage(event, myAccessibilityService);
        }
    }

    @Override
    public boolean canHandler(String packageName2) {
        return packageName.equals(packageName2);
    }
}