package miui.systemui.dynamicisland.window;

import L0.w;
import M0.x;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.Lifecycle;
import android.view.LifecycleOwner;
import android.view.LifecycleOwnerKt;
import android.view.LifecycleRegistry;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.annotation.CallSuper;
import androidx.core.view.OneShotPreDrawListener;
import androidx.mediarouter.media.MediaRouter;
import com.android.systemui.plugins.miui.dynamicisland.DynamicIslandContent;
import com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData;
import com.android.systemui.settings.UserTracker;
import com.miui.circulate.device.api.Column;
import com.miui.maml.folme.AnimatedProperty;
import com.xiaomi.onetrack.api.ah;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.q;
import miui.systemui.controlcenter.events.QsFlipEventsKt;
import miui.systemui.dynamicisland.DynamicIslandBackgroundView;
import miui.systemui.dynamicisland.DynamicIslandConstants;
import miui.systemui.dynamicisland.DynamicIslandUtils;
import miui.systemui.dynamicisland.R;
import miui.systemui.dynamicisland.dagger.DynamicIslandViewComponent;
import miui.systemui.dynamicisland.event.DynamicIslandEvent;
import miui.systemui.dynamicisland.event.DynamicIslandEventCoordinator;
import miui.systemui.dynamicisland.event.DynamicIslandState;
import miui.systemui.dynamicisland.event.handler.AppStateHandler;
import miui.systemui.dynamicisland.event.handler.BigIslandStateHandler;
import miui.systemui.dynamicisland.event.handler.ExpandedStateHandler;
import miui.systemui.dynamicisland.event.handler.MiniWindowStateHandler;
import miui.systemui.dynamicisland.event.handler.SmallIslandStateHandler;
import miui.systemui.dynamicisland.model.IslandTemplate;
import miui.systemui.dynamicisland.touch.TouchEvent;
import miui.systemui.dynamicisland.window.DynamicIslandWindowState;
import miui.systemui.dynamicisland.window.DynamicIslandWindowViewController;
import miui.systemui.dynamicisland.window.content.DynamicIslandBaseContentView;
import miui.systemui.dynamicisland.window.content.DynamicIslandBaseContentViewController;
import miui.systemui.dynamicisland.window.content.DynamicIslandContentFakeView;
import miui.systemui.dynamicisland.window.content.DynamicIslandContentView;
import miui.systemui.dynamicisland.window.content.DynamicIslandContentViewController;
import miui.systemui.dynamicisland.window.content.ui.viewbinder.DynamicIslandBaseContentViewBinder;
import miui.systemui.quicksettings.soundeffect.dirac.AudioEffectCenter;
import miui.systemui.util.CommonUtils;
import miui.systemui.util.FlipUtils;
import miui.systemui.util.FoldUtils;
import miui.systemui.util.MiBlurCompat;
import miui.systemui.util.ReflectBuilderUtil;
import miuix.android.content.MiuiIntent;
import v2.AbstractC1178g;
import v2.E;
import y2.A;
import y2.AbstractC1235h;
import y2.I;
import y2.InterfaceC1234g;
import y2.t;
import y2.y;

@Metadata(d1 = {"\u0000 \u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010 \n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010!\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\u0018\u0000 »\u00022\u00020\u00012\u00020\u00022\u00020\u0003:\u0002»\u0002B\u0019\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\b\u0010\tJ!\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000b\u001a\u00020\n2\b\u0010\f\u001a\u0004\u0018\u00010\nH\u0002¢\u0006\u0004\b\u000e\u0010\u000fJ/\u0010\u0017\u001a\u00020\u00162\b\u0010\u0011\u001a\u0004\u0018\u00010\u00102\b\u0010\u0013\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0014H\u0002¢\u0006\u0004\b\u0017\u0010\u0018J\u000f\u0010\u0019\u001a\u00020\u0016H\u0002¢\u0006\u0004\b\u0019\u0010\u001aJ\u000f\u0010\u001b\u001a\u00020\u0016H\u0002¢\u0006\u0004\b\u001b\u0010\u001aJ\u0011\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0002¢\u0006\u0004\b\u001d\u0010\u001eJ\u0011\u0010\u001f\u001a\u0004\u0018\u00010\u001cH\u0002¢\u0006\u0004\b\u001f\u0010\u001eJ)\u0010#\u001a\u0004\u0018\u00010\"2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\u001cH\u0002¢\u0006\u0004\b#\u0010$J'\u0010&\u001a\u00020%2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\u001cH\u0002¢\u0006\u0004\b&\u0010'J\u0019\u0010(\u001a\u00020\r2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0012H\u0002¢\u0006\u0004\b(\u0010)J\u0019\u0010+\u001a\u00020\r2\b\u0010*\u001a\u0004\u0018\u00010\"H\u0002¢\u0006\u0004\b+\u0010,J\u0017\u0010-\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u0012H\u0002¢\u0006\u0004\b-\u0010.J\u0019\u00101\u001a\u00020\u00162\b\u00100\u001a\u0004\u0018\u00010/H\u0002¢\u0006\u0004\b1\u00102J)\u00107\u001a\u00020\r2\u0006\u00103\u001a\u00020\u001c2\u0006\u00104\u001a\u00020\u001c2\b\u00106\u001a\u0004\u0018\u000105H\u0002¢\u0006\u0004\b7\u00108J\u0019\u0010;\u001a\u00020\u00142\b\u0010:\u001a\u0004\u0018\u000109H\u0002¢\u0006\u0004\b;\u0010<J\u000f\u0010=\u001a\u00020\rH\u0002¢\u0006\u0004\b=\u0010>J\u000f\u0010?\u001a\u00020\u0016H\u0002¢\u0006\u0004\b?\u0010\u001aJ\u000f\u0010@\u001a\u00020\u0016H\u0002¢\u0006\u0004\b@\u0010\u001aJ\u000f\u0010A\u001a\u00020\rH\u0002¢\u0006\u0004\bA\u0010>J\u0017\u0010B\u001a\u00020\u00162\u0006\u0010A\u001a\u00020\rH\u0002¢\u0006\u0004\bB\u0010CJ\u0017\u0010E\u001a\u00020\u00162\u0006\u0010D\u001a\u00020\rH\u0002¢\u0006\u0004\bE\u0010CJ\r\u0010F\u001a\u00020\u0016¢\u0006\u0004\bF\u0010\u001aJ\u0015\u0010G\u001a\u00020\u00162\u0006\u0010\f\u001a\u00020\n¢\u0006\u0004\bG\u0010HJ\u0017\u0010I\u001a\u00020\r2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0012¢\u0006\u0004\bI\u0010)J\u000f\u0010J\u001a\u00020\u0016H\u0014¢\u0006\u0004\bJ\u0010\u001aJ\u0017\u0010L\u001a\u0004\u0018\u00010/2\u0006\u0010K\u001a\u00020/¢\u0006\u0004\bL\u0010MJ\u0015\u0010N\u001a\u00020\u00162\u0006\u0010\u0015\u001a\u00020\u0014¢\u0006\u0004\bN\u0010OJ\u001d\u0010R\u001a\u00020\u00162\u0006\u0010P\u001a\u00020\u00142\u0006\u0010Q\u001a\u00020\u0014¢\u0006\u0004\bR\u0010SJ\u0015\u0010U\u001a\u00020\u00162\u0006\u0010T\u001a\u00020\r¢\u0006\u0004\bU\u0010CJ\u0015\u0010V\u001a\u00020\r2\u0006\u0010Q\u001a\u00020\u0014¢\u0006\u0004\bV\u0010WJ5\u0010]\u001a\u00020\u00162\b\u0010Y\u001a\u0004\u0018\u00010X2\b\u0010Z\u001a\u0004\u0018\u00010\u00142\b\u0010[\u001a\u0004\u0018\u00010X2\b\u0010\\\u001a\u0004\u0018\u00010\u0014¢\u0006\u0004\b]\u0010^J5\u0010_\u001a\u00020\u00162\b\u0010Y\u001a\u0004\u0018\u00010X2\b\u0010Z\u001a\u0004\u0018\u00010\u00142\b\u0010[\u001a\u0004\u0018\u00010X2\b\u0010\\\u001a\u0004\u0018\u00010\u0014¢\u0006\u0004\b_\u0010^J\u0015\u0010b\u001a\u00020\u00162\u0006\u0010a\u001a\u00020`¢\u0006\u0004\bb\u0010cJ\u001f\u0010f\u001a\u00020\u00162\u0006\u0010d\u001a\u00020\u00142\u0006\u0010e\u001a\u00020\u0004H\u0016¢\u0006\u0004\bf\u0010gJ\u001f\u0010k\u001a\u00020\r2\u0006\u0010h\u001a\u00020\r2\b\u0010j\u001a\u0004\u0018\u00010i¢\u0006\u0004\bk\u0010lJ\r\u0010m\u001a\u00020\u0014¢\u0006\u0004\bm\u0010nJ\r\u0010o\u001a\u00020\u0014¢\u0006\u0004\bo\u0010nJ\r\u0010p\u001a\u000205¢\u0006\u0004\bp\u0010qJ9\u0010s\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010h\u001a\u00020\r2\u0006\u0010 \u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\u001c2\b\b\u0002\u0010r\u001a\u00020\rH\u0007¢\u0006\u0004\bs\u0010tJ%\u0010u\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010h\u001a\u00020\r2\u0006\u0010 \u001a\u00020\u001c¢\u0006\u0004\bu\u0010vJ!\u0010x\u001a\u00020\u00162\b\u0010\u0013\u001a\u0004\u0018\u00010\u00122\b\b\u0002\u0010w\u001a\u00020\r¢\u0006\u0004\bx\u0010yJ5\u0010x\u001a\u00020\u00162\b\u0010[\u001a\u0004\u0018\u00010X2\b\u0010Y\u001a\u0004\u0018\u00010X2\b\b\u0002\u0010z\u001a\u00020\r2\b\b\u0002\u0010w\u001a\u00020\r¢\u0006\u0004\bx\u0010{J\u0019\u0010|\u001a\u0004\u0018\u00010\"2\b\u0010[\u001a\u0004\u0018\u00010X¢\u0006\u0004\b|\u0010}J+\u0010~\u001a\u00020\u00162\b\u0010\u0013\u001a\u0004\u0018\u00010\u00122\b\u0010[\u001a\u0004\u0018\u00010X2\b\b\u0002\u0010z\u001a\u00020\r¢\u0006\u0004\b~\u0010\u007fJ\u001a\u0010\u0080\u0001\u001a\u00020\u00162\b\u0010\u0011\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u0080\u0001\u0010\u0081\u0001J#\u0010\u0083\u0001\u001a\r\u0012\u0006\u0012\u0004\u0018\u00010\"\u0018\u00010\u0082\u00012\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b\u0083\u0001\u0010\u0084\u0001J\u0019\u0010\u0085\u0001\u001a\u0004\u0018\u00010\"2\u0006\u0010Y\u001a\u00020X¢\u0006\u0005\b\u0085\u0001\u0010}J\u0018\u0010\u0086\u0001\u001a\u00020\u00142\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b\u0086\u0001\u0010\u0087\u0001J\u0019\u0010\u0088\u0001\u001a\u0004\u0018\u00010\"2\u0006\u0010Y\u001a\u00020X¢\u0006\u0005\b\u0088\u0001\u0010}J\u0012\u0010\u0089\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u0089\u0001\u0010\u008a\u0001J\u0012\u0010\u008b\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u008b\u0001\u0010\u008a\u0001J\u001b\u0010\u008d\u0001\u001a\u00020\u00142\t\b\u0002\u0010\u008c\u0001\u001a\u00020\r¢\u0006\u0006\b\u008d\u0001\u0010\u008e\u0001J\u0012\u0010\u008f\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u008f\u0001\u0010\u008a\u0001J\u0018\u0010\u0090\u0001\u001a\u00020\r2\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b\u0090\u0001\u0010\u0091\u0001J\u0018\u0010\u0092\u0001\u001a\u00020\r2\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b\u0092\u0001\u0010\u0091\u0001J\u0019\u0010\u0093\u0001\u001a\u00020\r2\b\u0010\u0011\u001a\u0004\u0018\u00010\"¢\u0006\u0005\b\u0093\u0001\u0010,J\u000f\u0010\u0094\u0001\u001a\u00020\r¢\u0006\u0005\b\u0094\u0001\u0010>J\u001b\u0010\u0096\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u0096\u0001\u0010\u0081\u0001J\u001e\u0010\u0098\u0001\u001a\u0005\u0018\u00010\u0097\u00012\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u0098\u0001\u0010\u0099\u0001J\u001a\u0010\u009a\u0001\u001a\u0004\u0018\u00010%2\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b\u009a\u0001\u0010\u009b\u0001J\u0019\u0010\u009c\u0001\u001a\u0004\u0018\u00010\"2\u0006\u0010Y\u001a\u00020X¢\u0006\u0005\b\u009c\u0001\u0010}J\u0012\u0010\u009d\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u009d\u0001\u0010\u008a\u0001J\u0012\u0010\u009e\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u009e\u0001\u0010\u008a\u0001J\u0012\u0010\u009f\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b\u009f\u0001\u0010\u008a\u0001J\u0012\u0010 \u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b \u0001\u0010\u008a\u0001J\u0019\u0010¢\u0001\u001a\u000b\u0012\u0006\u0012\u0004\u0018\u00010\"0¡\u0001¢\u0006\u0006\b¢\u0001\u0010£\u0001J\u001f\u0010¤\u0001\u001a\t\u0012\u0004\u0012\u00020\"0\u0082\u00012\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b¤\u0001\u0010\u0084\u0001J\u000f\u0010¥\u0001\u001a\u00020\r¢\u0006\u0005\b¥\u0001\u0010>J\u0019\u0010§\u0001\u001a\u00020\u00162\u0007\u0010¦\u0001\u001a\u00020X¢\u0006\u0006\b§\u0001\u0010¨\u0001J\u001b\u0010©\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b©\u0001\u0010\u0081\u0001J\u001b\u0010ª\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\bª\u0001\u0010\u0081\u0001J$\u0010¬\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"2\u0007\u0010«\u0001\u001a\u00020\r¢\u0006\u0006\b¬\u0001\u0010\u00ad\u0001J-\u0010¯\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"2\u0007\u0010«\u0001\u001a\u00020\r2\u0007\u0010®\u0001\u001a\u00020\r¢\u0006\u0006\b¯\u0001\u0010°\u0001J\u001b\u0010±\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b±\u0001\u0010\u0081\u0001J\u001b\u0010²\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b²\u0001\u0010\u0081\u0001J\u001b\u0010³\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b³\u0001\u0010\u0081\u0001J\u0018\u0010´\u0001\u001a\u00020\u00162\u0006\u0010Y\u001a\u00020X¢\u0006\u0006\b´\u0001\u0010¨\u0001J%\u0010¶\u0001\u001a\u00020\u00162\b\u00106\u001a\u0004\u0018\u0001052\t\u0010µ\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\b¶\u0001\u0010·\u0001J\u001a\u0010¹\u0001\u001a\u00020\u00162\u0007\u0010¸\u0001\u001a\u00020\rH\u0007¢\u0006\u0005\b¹\u0001\u0010CJ%\u0010½\u0001\u001a\u00020\u00162\u0007\u0010º\u0001\u001a\u00020\r2\n\u0010¼\u0001\u001a\u0005\u0018\u00010»\u0001¢\u0006\u0006\b½\u0001\u0010¾\u0001J\u0018\u0010À\u0001\u001a\u00020\u00162\u0007\u0010¿\u0001\u001a\u00020\r¢\u0006\u0005\bÀ\u0001\u0010CJ\u000f\u0010Á\u0001\u001a\u00020\u0016¢\u0006\u0005\bÁ\u0001\u0010\u001aJ,\u0010Ã\u0001\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u00102\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"2\u0007\u0010Â\u0001\u001a\u00020\u001c¢\u0006\u0006\bÃ\u0001\u0010Ä\u0001J\u0018\u0010Å\u0001\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u0010¢\u0006\u0006\bÅ\u0001\u0010Æ\u0001J\u001e\u0010É\u0001\u001a\u00020\r2\n\u0010È\u0001\u001a\u0005\u0018\u00010Ç\u0001H\u0016¢\u0006\u0006\bÉ\u0001\u0010Ê\u0001J\u001e\u0010Ì\u0001\u001a\u00020\r2\n\u0010Ë\u0001\u001a\u0005\u0018\u00010Ç\u0001H\u0017¢\u0006\u0006\bÌ\u0001\u0010Ê\u0001J\u0019\u0010Î\u0001\u001a\u00020\r2\u0007\u0010Í\u0001\u001a\u00020\u001c¢\u0006\u0006\bÎ\u0001\u0010Ï\u0001J3\u0010Ñ\u0001\u001a\u00020\r2\u0007\u0010Ð\u0001\u001a\u00020\u00042\u0006\u00103\u001a\u00020\u001c2\u0006\u00104\u001a\u00020\u001c2\b\u0010\u0011\u001a\u0004\u0018\u00010i¢\u0006\u0006\bÑ\u0001\u0010Ò\u0001J\u001c\u0010Ó\u0001\u001a\u0002092\b\u0010:\u001a\u0004\u0018\u000109H\u0016¢\u0006\u0006\bÓ\u0001\u0010Ô\u0001J\u0018\u0010Õ\u0001\u001a\u00020\u00162\u0006\u00106\u001a\u000205¢\u0006\u0006\bÕ\u0001\u0010Ö\u0001J\u001c\u0010Ù\u0001\u001a\u00020\u00162\b\u0010Ø\u0001\u001a\u00030×\u0001H\u0014¢\u0006\u0006\bÙ\u0001\u0010Ú\u0001J\"\u0010Ü\u0001\u001a\u00020\u00162\u0007\u0010Û\u0001\u001a\u00020\r2\u0007\u0010«\u0001\u001a\u00020\r¢\u0006\u0006\bÜ\u0001\u0010Ý\u0001J,\u0010ß\u0001\u001a\u00020\u00162\u0007\u0010Þ\u0001\u001a\u00020\r2\b\u0010\u0011\u001a\u0004\u0018\u00010\"2\u0007\u0010«\u0001\u001a\u00020\r¢\u0006\u0006\bß\u0001\u0010à\u0001J\u001b\u0010á\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\bá\u0001\u0010\u0081\u0001J\u001b\u0010â\u0001\u001a\u00020\u00162\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\"¢\u0006\u0006\bâ\u0001\u0010\u0081\u0001J\u0018\u0010ã\u0001\u001a\u00020\r2\u0006\u0010[\u001a\u00020X¢\u0006\u0006\bã\u0001\u0010\u0091\u0001J\u000f\u0010ä\u0001\u001a\u00020\u0016¢\u0006\u0005\bä\u0001\u0010\u001aJ.\u0010ç\u0001\u001a\u00020\u00162\b\u0010\u0011\u001a\u0004\u0018\u00010\"2\t\u0010å\u0001\u001a\u0004\u0018\u00010\u001c2\u0007\u0010æ\u0001\u001a\u00020\r¢\u0006\u0006\bç\u0001\u0010è\u0001J\u000f\u0010é\u0001\u001a\u00020\u0016¢\u0006\u0005\bé\u0001\u0010\u001aJ\u000f\u0010ê\u0001\u001a\u00020\r¢\u0006\u0005\bê\u0001\u0010>J.\u0010ï\u0001\u001a\u00020\u00162\u0007\u0010ë\u0001\u001a\u00020i2\u0007\u0010ì\u0001\u001a\u00020\u00142\b\u0010î\u0001\u001a\u00030í\u0001H\u0017¢\u0006\u0006\bï\u0001\u0010ð\u0001R\u001c\u0010ò\u0001\u001a\u0005\u0018\u00010ñ\u00018\u0002@\u0002X\u0082\u000e¢\u0006\b\n\u0006\bò\u0001\u0010ó\u0001R!\u0010ù\u0001\u001a\u00030ô\u00018FX\u0086\u0084\u0002¢\u0006\u0010\n\u0006\bõ\u0001\u0010ö\u0001\u001a\u0006\b÷\u0001\u0010ø\u0001R\u0019\u0010a\u001a\u0004\u0018\u00010`8\u0002@\u0002X\u0082\u000e¢\u0006\u0007\n\u0005\ba\u0010ú\u0001R0\u0010ü\u0001\u001a\t\u0012\u0004\u0012\u00020\"0û\u00018\u0006@\u0006X\u0086\u000e¢\u0006\u0018\n\u0006\bü\u0001\u0010ý\u0001\u001a\u0006\bþ\u0001\u0010ÿ\u0001\"\u0006\b\u0080\u0002\u0010\u0081\u0002R \u0010\u0082\u0002\u001a\t\u0012\u0004\u0012\u00020%0û\u00018\u0002@\u0002X\u0082\u000e¢\u0006\b\n\u0006\b\u0082\u0002\u0010ý\u0001R,\u0010\u0084\u0002\u001a\u0005\u0018\u00010\u0083\u00028\u0006@\u0006X\u0086\u000e¢\u0006\u0018\n\u0006\b\u0084\u0002\u0010\u0085\u0002\u001a\u0006\b\u0086\u0002\u0010\u0087\u0002\"\u0006\b\u0088\u0002\u0010\u0089\u0002R\u001a\u0010\u008b\u0002\u001a\u00030\u008a\u00028\u0002@\u0002X\u0082.¢\u0006\b\n\u0006\b\u008b\u0002\u0010\u008c\u0002R\u001a\u0010\u008d\u0002\u001a\u00030\u008a\u00028\u0002@\u0002X\u0082.¢\u0006\b\n\u0006\b\u008d\u0002\u0010\u008c\u0002R\u001e\u0010\u008e\u0002\u001a\t\u0012\u0004\u0012\u00020\u00120¡\u00018\u0002X\u0082\u0004¢\u0006\b\n\u0006\b\u008e\u0002\u0010\u008f\u0002R\u0019\u0010\u0090\u0002\u001a\u00020\u00148\u0002@\u0002X\u0082\u000e¢\u0006\b\n\u0006\b\u0090\u0002\u0010\u0091\u0002R'\u0010¸\u0001\u001a\u00020\r8\u0006@\u0006X\u0086\u000e¢\u0006\u0016\n\u0006\b¸\u0001\u0010\u0092\u0002\u001a\u0005\b¸\u0001\u0010>\"\u0005\b\u0093\u0002\u0010CR\u0019\u0010\u0094\u0002\u001a\u00020\u00148\u0002@\u0002X\u0082\u000e¢\u0006\b\n\u0006\b\u0094\u0002\u0010\u0091\u0002R\u001f\u0010\u0096\u0002\u001a\n\u0012\u0005\u0012\u00030×\u00010\u0095\u00028\u0002X\u0082\u0004¢\u0006\b\n\u0006\b\u0096\u0002\u0010\u0097\u0002R$\u0010\u0099\u0002\u001a\n\u0012\u0005\u0012\u00030×\u00010\u0098\u00028\u0006¢\u0006\u0010\n\u0006\b\u0099\u0002\u0010\u009a\u0002\u001a\u0006\b\u009b\u0002\u0010\u009c\u0002R!\u0010¡\u0002\u001a\u00030\u009d\u00028VX\u0096\u0084\u0002¢\u0006\u0010\n\u0006\b\u009e\u0002\u0010ö\u0001\u001a\u0006\b\u009f\u0002\u0010 \u0002R\u0018\u0010£\u0002\u001a\u00030¢\u00028\u0002X\u0082\u0004¢\u0006\b\n\u0006\b£\u0002\u0010¤\u0002R\u0017\u0010\u000b\u001a\u00020\n8\u0002@\u0002X\u0082.¢\u0006\u0007\n\u0005\b\u000b\u0010¥\u0002R\u0019\u0010¦\u0002\u001a\u00020\r8\u0002@\u0002X\u0082\u000e¢\u0006\b\n\u0006\b¦\u0002\u0010\u0092\u0002R\u001c\u0010¨\u0002\u001a\u0005\u0018\u00010§\u00028\u0002@\u0002X\u0082\u000e¢\u0006\b\n\u0006\b¨\u0002\u0010©\u0002R\u001f\u0010«\u0002\u001a\n\u0012\u0005\u0012\u00030ª\u00020\u0095\u00028\u0002X\u0082\u0004¢\u0006\b\n\u0006\b«\u0002\u0010\u0097\u0002R\u001f\u0010¬\u0002\u001a\n\u0012\u0005\u0012\u00030ª\u00020\u0095\u00028\u0002X\u0082\u0004¢\u0006\b\n\u0006\b¬\u0002\u0010\u0097\u0002R$\u0010É\u0001\u001a\n\u0012\u0005\u0012\u00030ª\u00020\u0098\u00028\u0006¢\u0006\u0010\n\u0006\bÉ\u0001\u0010\u009a\u0002\u001a\u0006\b\u00ad\u0002\u0010\u009c\u0002R$\u0010Ì\u0001\u001a\n\u0012\u0005\u0012\u00030ª\u00020\u0098\u00028\u0006¢\u0006\u0010\n\u0006\bÌ\u0001\u0010\u009a\u0002\u001a\u0006\b®\u0002\u0010\u009c\u0002R,\u0010´\u0002\u001a\u00030ñ\u00012\b\u0010¯\u0002\u001a\u00030ñ\u00018F@FX\u0086\u000e¢\u0006\u0010\u001a\u0006\b°\u0002\u0010±\u0002\"\u0006\b²\u0002\u0010³\u0002R$\u0010·\u0002\u001a\u0012\u0012\r\u0012\u000b µ\u0002*\u0004\u0018\u00010%0%0\u0082\u00018F¢\u0006\b\u001a\u0006\b¶\u0002\u0010£\u0001R(\u0010º\u0002\u001a\u00020\u00142\u0007\u0010¯\u0002\u001a\u00020\u00148F@FX\u0086\u000e¢\u0006\u000e\u001a\u0005\b¸\u0002\u0010n\"\u0005\b¹\u0002\u0010O¨\u0006¼\u0002"}, d2 = {"Lmiui/systemui/dynamicisland/window/DynamicIslandWindowView;", "Landroid/widget/FrameLayout;", "Landroidx/lifecycle/LifecycleOwner;", "Lcom/android/systemui/settings/UserTracker$Callback;", "Landroid/content/Context;", "context", "Landroid/util/AttributeSet;", "attributeSet", "<init>", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "Landroid/content/res/Configuration;", "previousConfig", "newConfig", "", "compareConfigurations", "(Landroid/content/res/Configuration;Landroid/content/res/Configuration;)Z", "Lmiui/systemui/dynamicisland/window/content/DynamicIslandBaseContentView;", ah.ae, "Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;", "dynamicIslandData", "", "height", "LL0/w;", "updateExpandedView", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandBaseContentView;Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;Ljava/lang/Integer;)V", "initEventCoordinator", "()V", "requestImmersiveMode", "", "requestCutoutY", "()Ljava/lang/Float;", "requestMaxIslandWidth", "maxWidth", "cutoutY", "Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;", "buildDynamicIslandContentView", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;FF)Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;", "Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentFakeView;", "buildDynamicIslandFakeContentView", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;FF)Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentFakeView;", "assertUserSpace", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;)Z", "update", "canUpdate", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;)Z", "updateDynamicIslandDataList", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;)V", "Landroid/os/Bundle;", "extras", "sendExitPendingIntent", "(Landroid/os/Bundle;)V", "touchX", "touchY", "Landroid/graphics/Rect;", "rect", "isTouchInsideRect", "(FFLandroid/graphics/Rect;)Z", "Landroid/view/WindowInsets;", "insets", "getWindowInsetsRotation", "(Landroid/view/WindowInsets;)I", "isTinyScreen", "()Z", "removeTempShowBigIslandOnFlipTinyChanged", "onDynamicIslandDataChanged", "hasDeviceNotification", "onDeviceNotificationChanged", "(Z)V", "supportBLur", "updateBlurContainer", "destroy", "onConfigChanged", "(Landroid/content/res/Configuration;)V", "isMediaApp", "onFinishInflate", AudioEffectCenter.KEY_BUNDLE, "notifyIslandViewChanged", "(Landroid/os/Bundle;)Landroid/os/Bundle;", "updateHeadsUpZone", "(I)V", AnimatedProperty.PROPERTY_NAME_X, AnimatedProperty.PROPERTY_NAME_Y, "maybeCollapseExpand", "(II)V", "visible", "updateStatusBarVisible", "touchInHeadsUpZone", "(I)Z", "", "pkg", "uid", "key", "prop", "notifyAddIsland", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/Integer;)V", "notifyRemoveIsland", "Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandContent$DynamicIslandViewChangedListener;", "listener", "addOnDynamicIslandViewChangedListener", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandContent$DynamicIslandViewChangedListener;)V", "newUser", "userContext", "onUserChanged", "(ILandroid/content/Context;)V", "expanded", "Landroid/view/View;", "expandedView", "canExpanded", "(ZLandroid/view/View;)Z", "getCutoutWidth", "()I", "getCutoutHeight", "getCutoutRect", "()Landroid/graphics/Rect;", "addToHistoryList", "addDynamicIslandData", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;ZFFZ)V", "updateDynamicIslandView", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;ZF)V", "noAnimation", "removeDynamicIslandData", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;Z)V", "removeFromList", "(Ljava/lang/String;Ljava/lang/String;ZZ)V", "getViewFromList", "(Ljava/lang/String;)Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;", "clearAfterDelete", "(Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandData;Ljava/lang/String;Z)V", "appExit", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;)V", "", "requestHasIsland", "(Ljava/lang/String;)Ljava/util/List;", "getMainMiniWindow", "getMainMiniWindowTopLeveCount", "(Ljava/lang/String;)I", "getMainMiniWindowTopLeve", "getMainAppExpanded", "()Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;", "getSubAppExpanded", "bigIsland", "getMainAppExpandedTopLeveCount", "(Z)I", "getMainAppExpandedTopLeve", "hasSubMiniWindow", "(Ljava/lang/String;)Z", "hasSubAppExpanded", "hasOtherBigIsland", "hasNoActiveDynamicIsland", "state", "appEnter", "Lmiui/systemui/dynamicisland/event/DynamicIslandState;", "calculateCollapse", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;)Lmiui/systemui/dynamicisland/event/DynamicIslandState;", "getMiniWindowContentFakeView", "(Ljava/lang/String;)Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentFakeView;", "getMainMiniWindowState", "getCurrentBigIslandState", "getCurrentTempShowBigIslandState", "getCurrentSmallIslandState", "getCurrentExpandedState", "", "getCurrentAppExpandedState", "()Ljava/util/List;", "getCurrentMiniWindowState", "isUserExpanded", "reason", "collapse", "(Ljava/lang/String;)V", "enterMiniWindow", "exitMiniWindow", "isFreeform", "updateViewStateWhenCloseEnd", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;Z)V", "closingToExpanded", "setClosingToExpanded", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;ZZ)V", "updateViewStateWhenOpenAnimStart", "updateFreeformFakeView", "cancelExpandViewTrackingAnim", "updatePkgSupportFreeform", "realView", "updateAppCloseRect", "(Landroid/graphics/Rect;Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;)V", "isLight", "updateDarkLightMode", MiuiIntent.COMMAND_ICON_PANEL_HIDE, "Lmiui/systemui/dynamicisland/window/DynamicIslandWindowState$TempHiddenType;", "type", "onIslandTempHide", "(ZLmiui/systemui/dynamicisland/window/DynamicIslandWindowState$TempHiddenType;)V", "isKeyguardShowing", "onKeyguardShowing", "updateTouchRegion", "rawY", "onLongPress", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandBaseContentView;Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;F)V", "cancelLongPressRunnable", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandBaseContentView;)V", "Landroid/view/MotionEvent;", "ev", "onInterceptTouchEvent", "(Landroid/view/MotionEvent;)Z", "event", "onTouchEvent", "diffx", "isSwipeTowardsSmallIsland", "(F)Z", "sysUIContext", "isDownInSeekBar", "(Landroid/content/Context;FFLandroid/view/View;)Z", "onApplyWindowInsets", "(Landroid/view/WindowInsets;)Landroid/view/WindowInsets;", "setEffectSize", "(Landroid/graphics/Rect;)V", "Landroid/graphics/Canvas;", "canvas", "dispatchDraw", "(Landroid/graphics/Canvas;)V", "animRunning", "setAnimRunning", "(ZZ)V", "windowAnimRunning", "updateIslandWindowAnimRunningState", "(ZLmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;Z)V", "onWindowAnimExtendLifetimeEnd", "onWindowAnimExtendLifetimeStart", "needExtendLifetime", "enterMiniWindowEnd", "progress", "reset", "updateExpandedViewScaleForFreeform", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;Ljava/lang/Float;Z)V", "hideAllElementSurface", "isTempShowBigIslandToBeRemoved", "child", QsFlipEventsKt.EVENT_KEY_FLIP_QS_INDEX, "Landroid/view/ViewGroup$LayoutParams;", "params", "addView", "(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V", "Lmiui/systemui/dynamicisland/dagger/DynamicIslandViewComponent;", "_viewComponent", "Lmiui/systemui/dynamicisland/dagger/DynamicIslandViewComponent;", "Lmiui/systemui/dynamicisland/window/DynamicIslandWindowViewController;", "windowViewController$delegate", "LL0/g;", "getWindowViewController", "()Lmiui/systemui/dynamicisland/window/DynamicIslandWindowViewController;", "windowViewController", "Lcom/android/systemui/plugins/miui/dynamicisland/DynamicIslandContent$DynamicIslandViewChangedListener;", "Ljava/util/concurrent/CopyOnWriteArrayList;", "contentViewList", "Ljava/util/concurrent/CopyOnWriteArrayList;", "getContentViewList", "()Ljava/util/concurrent/CopyOnWriteArrayList;", "setContentViewList", "(Ljava/util/concurrent/CopyOnWriteArrayList;)V", "fakeViewList", "Lmiui/systemui/dynamicisland/event/DynamicIslandEventCoordinator;", "eventCoordinator", "Lmiui/systemui/dynamicisland/event/DynamicIslandEventCoordinator;", "getEventCoordinator", "()Lmiui/systemui/dynamicisland/event/DynamicIslandEventCoordinator;", "setEventCoordinator", "(Lmiui/systemui/dynamicisland/event/DynamicIslandEventCoordinator;)V", "Landroid/view/ViewGroup;", "mGlowEffectBottomContainer", "Landroid/view/ViewGroup;", "mGlowEffectTopContainer", "dynamicIslandDataList", "Ljava/util/List;", "currentUserId", "I", "Z", "setLight", "_headsUpHeight", "Ly2/t;", "_dispatchDrawEvent", "Ly2/t;", "Ly2/y;", "dispatchDrawEvent", "Ly2/y;", "getDispatchDrawEvent", "()Ly2/y;", "Landroidx/lifecycle/LifecycleRegistry;", "lifecycle$delegate", "getLifecycle", "()Landroidx/lifecycle/LifecycleRegistry;", "lifecycle", "Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;", "onGlobalLayoutListener", "Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;", "Landroid/content/res/Configuration;", "touchOutsideInHeadsUp", "Ljava/lang/Runnable;", "longPress", "Ljava/lang/Runnable;", "Lmiui/systemui/dynamicisland/touch/TouchEvent;", "_onInterceptTouchEvent", "_onTouchEvent", "getOnInterceptTouchEvent", "getOnTouchEvent", ah.f5099p, "getViewComponent", "()Lmiui/systemui/dynamicisland/dagger/DynamicIslandViewComponent;", "setViewComponent", "(Lmiui/systemui/dynamicisland/dagger/DynamicIslandViewComponent;)V", "viewComponent", "kotlin.jvm.PlatformType", "getFakeViews", "fakeViews", "getHeadsUpHeight", "setHeadsUpHeight", "headsUpHeight", "Companion", "miui-dynamicisland_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class DynamicIslandWindowView extends FrameLayout implements LifecycleOwner, UserTracker.Callback {
    private static final int SWIPE_THRESHOLD = 100;
    private static final String TAG = "DynamicIslandWindowViewImpl";
    private final t _dispatchDrawEvent;
    private int _headsUpHeight;
    private final t _onInterceptTouchEvent;
    private final t _onTouchEvent;
    private DynamicIslandViewComponent _viewComponent;
    private CopyOnWriteArrayList<DynamicIslandContentView> contentViewList;
    private int currentUserId;
    private final y dispatchDrawEvent;
    private final List<DynamicIslandData> dynamicIslandDataList;
    private DynamicIslandEventCoordinator eventCoordinator;
    private CopyOnWriteArrayList<DynamicIslandContentFakeView> fakeViewList;
    private boolean isLight;

    /* renamed from: lifecycle$delegate, reason: from kotlin metadata */
    private final L0.g lifecycle;
    private DynamicIslandContent.DynamicIslandViewChangedListener listener;
    private Runnable longPress;
    private ViewGroup mGlowEffectBottomContainer;
    private ViewGroup mGlowEffectTopContainer;
    private final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private final y onInterceptTouchEvent;
    private final y onTouchEvent;
    private Configuration previousConfig;
    private boolean touchOutsideInHeadsUp;

    /* renamed from: windowViewController$delegate, reason: from kotlin metadata */
    private final L0.g windowViewController;

    @Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "it", "Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;", "kotlin.jvm.PlatformType", "invoke", "(Lmiui/systemui/dynamicisland/window/content/DynamicIslandContentView;)Ljava/lang/Boolean;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$buildDynamicIslandContentView$1, reason: invalid class name */
    public static final class AnonymousClass1 extends q implements Function1 {
        final /* synthetic */ DynamicIslandData $dynamicIslandData;
        final /* synthetic */ DynamicIslandWindowView this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass1(DynamicIslandData dynamicIslandData, DynamicIslandWindowView dynamicIslandWindowView) {
            super(1);
            this.$dynamicIslandData = dynamicIslandData;
            this.this$0 = dynamicIslandWindowView;
        }

        @Override // kotlin.jvm.functions.Function1
        public final Boolean invoke(DynamicIslandContentView dynamicIslandContentView) {
            DynamicIslandData currentIslandData = dynamicIslandContentView.getCurrentIslandData();
            boolean zC = kotlin.jvm.internal.o.c(currentIslandData != null ? currentIslandData.getKey() : null, this.$dynamicIslandData.getKey());
            if (zC) {
                this.this$0.removeView(dynamicIslandContentView.getBackgroundView());
                dynamicIslandContentView.getController().destroy();
            }
            return Boolean.valueOf(zC);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$dispatchDraw$1", f = "DynamicIslandWindowView.kt", l = {1070}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$dispatchDraw$1, reason: invalid class name and case insensitive filesystem */
    public static final class C09001 extends R0.l implements Z0.o {
        final /* synthetic */ Canvas $canvas;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C09001(Canvas canvas, P0.d dVar) {
            super(2, dVar);
            this.$canvas = canvas;
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new C09001(this.$canvas, dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                t tVar = DynamicIslandWindowView.this._dispatchDrawEvent;
                Canvas canvas = this.$canvas;
                this.label = 1;
                if (tVar.emit(canvas, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            return w.f1623a;
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((C09001) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$1", f = "DynamicIslandWindowView.kt", l = {MediaRouter.GlobalMediaRouter.CallbackHandler.MSG_ROUTE_ANOTHER_SELECTED}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$1, reason: invalid class name and case insensitive filesystem */
    public static final class C09011 extends R0.l implements Z0.o {
        int label;

        public C09011(P0.d dVar) {
            super(2, dVar);
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new C09011(dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            I islandRegion;
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                DynamicIslandEventCoordinator eventCoordinator = DynamicIslandWindowView.this.getEventCoordinator();
                if (eventCoordinator == null || (islandRegion = eventCoordinator.getIslandRegion()) == null) {
                    return w.f1623a;
                }
                final DynamicIslandWindowView dynamicIslandWindowView = DynamicIslandWindowView.this;
                InterfaceC1234g interfaceC1234g = new InterfaceC1234g() { // from class: miui.systemui.dynamicisland.window.DynamicIslandWindowView.initEventCoordinator.1.1
                    @Override // y2.InterfaceC1234g
                    public final Object emit(Region region, P0.d dVar) {
                        Log.e(DynamicIslandWindowView.TAG, "islandRegion " + region);
                        Bundle bundle = new Bundle();
                        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_BACK_ISLAND_WIDTH_CHANGED);
                        bundle.putParcelable(DynamicIslandConstants.EXTRA_BACK_ISLAND_REGION, region);
                        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = dynamicIslandWindowView.listener;
                        if (dynamicIslandViewChangedListener != null) {
                            dynamicIslandViewChangedListener.onIslandViewChanged(bundle);
                        }
                        return w.f1623a;
                    }
                };
                this.label = 1;
                if (islandRegion.collect(interfaceC1234g, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            throw new L0.d();
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((C09011) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$2", f = "DynamicIslandWindowView.kt", l = {273}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$2, reason: invalid class name */
    public static final class AnonymousClass2 extends R0.l implements Z0.o {
        int label;

        public AnonymousClass2(P0.d dVar) {
            super(2, dVar);
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new AnonymousClass2(dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            I bigIslandRegion;
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                DynamicIslandEventCoordinator eventCoordinator = DynamicIslandWindowView.this.getEventCoordinator();
                if (eventCoordinator == null || (bigIslandRegion = eventCoordinator.getBigIslandRegion()) == null) {
                    return w.f1623a;
                }
                InterfaceC1234g interfaceC1234g = new InterfaceC1234g() { // from class: miui.systemui.dynamicisland.window.DynamicIslandWindowView.initEventCoordinator.2.1
                    @Override // y2.InterfaceC1234g
                    public final Object emit(Region region, P0.d dVar) {
                        Log.e(DynamicIslandWindowView.TAG, "bigIslandRegion " + region);
                        return w.f1623a;
                    }
                };
                this.label = 1;
                if (bigIslandRegion.collect(interfaceC1234g, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            throw new L0.d();
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((AnonymousClass2) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$3", f = "DynamicIslandWindowView.kt", l = {278}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$3, reason: invalid class name */
    public static final class AnonymousClass3 extends R0.l implements Z0.o {
        int label;

        public AnonymousClass3(P0.d dVar) {
            super(2, dVar);
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new AnonymousClass3(dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            I expandedViewRegion;
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                DynamicIslandEventCoordinator eventCoordinator = DynamicIslandWindowView.this.getEventCoordinator();
                if (eventCoordinator == null || (expandedViewRegion = eventCoordinator.getExpandedViewRegion()) == null) {
                    return w.f1623a;
                }
                final DynamicIslandWindowView dynamicIslandWindowView = DynamicIslandWindowView.this;
                InterfaceC1234g interfaceC1234g = new InterfaceC1234g() { // from class: miui.systemui.dynamicisland.window.DynamicIslandWindowView.initEventCoordinator.3.1
                    @Override // y2.InterfaceC1234g
                    public final Object emit(Region region, P0.d dVar) {
                        Log.e(DynamicIslandWindowView.TAG, "expandedViewRegion " + region);
                        DynamicIslandEventCoordinator eventCoordinator2 = dynamicIslandWindowView.getEventCoordinator();
                        if (eventCoordinator2 != null) {
                            eventCoordinator2.handleExpandGestureListener(region);
                        }
                        return w.f1623a;
                    }
                };
                this.label = 1;
                if (expandedViewRegion.collect(interfaceC1234g, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            throw new L0.d();
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((AnonymousClass3) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$4", f = "DynamicIslandWindowView.kt", l = {284}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$initEventCoordinator$4, reason: invalid class name */
    public static final class AnonymousClass4 extends R0.l implements Z0.o {
        int label;

        public AnonymousClass4(P0.d dVar) {
            super(2, dVar);
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new AnonymousClass4(dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            I statusBarVisible;
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                DynamicIslandEventCoordinator eventCoordinator = DynamicIslandWindowView.this.getEventCoordinator();
                if (eventCoordinator == null || (statusBarVisible = eventCoordinator.getStatusBarVisible()) == null) {
                    return w.f1623a;
                }
                final DynamicIslandWindowView dynamicIslandWindowView = DynamicIslandWindowView.this;
                InterfaceC1234g interfaceC1234g = new InterfaceC1234g() { // from class: miui.systemui.dynamicisland.window.DynamicIslandWindowView.initEventCoordinator.4.1
                    @Override // y2.InterfaceC1234g
                    public final Object emit(Boolean bool, P0.d dVar) throws Resources.NotFoundException {
                        DynamicIslandEventCoordinator eventCoordinator2;
                        DynamicIslandEventCoordinator eventCoordinator3 = dynamicIslandWindowView.getEventCoordinator();
                        if (eventCoordinator3 != null) {
                            eventCoordinator3.updateTouchRegion();
                        }
                        if (kotlin.jvm.internal.o.c(bool, R0.b.a(false)) && dynamicIslandWindowView.getCurrentExpandedState() == null && (eventCoordinator2 = dynamicIslandWindowView.getEventCoordinator()) != null) {
                            eventCoordinator2.resetSwipe(0.0f, 0.0f);
                        }
                        return w.f1623a;
                    }
                };
                this.label = 1;
                if (statusBarVisible.collect(interfaceC1234g, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            throw new L0.d();
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((AnonymousClass4) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$onInterceptTouchEvent$1", f = "DynamicIslandWindowView.kt", l = {974}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$onInterceptTouchEvent$1, reason: invalid class name and case insensitive filesystem */
    public static final class C09021 extends R0.l implements Z0.o {
        final /* synthetic */ TouchEvent $touchEvent;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C09021(TouchEvent touchEvent, P0.d dVar) {
            super(2, dVar);
            this.$touchEvent = touchEvent;
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new C09021(this.$touchEvent, dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                t tVar = DynamicIslandWindowView.this._onInterceptTouchEvent;
                TouchEvent touchEvent = this.$touchEvent;
                this.label = 1;
                if (tVar.emit(touchEvent, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            return w.f1623a;
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((C09021) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$onTouchEvent$1", f = "DynamicIslandWindowView.kt", l = {984}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$onTouchEvent$1, reason: invalid class name and case insensitive filesystem */
    public static final class C09031 extends R0.l implements Z0.o {
        final /* synthetic */ TouchEvent $touchEvent;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C09031(TouchEvent touchEvent, P0.d dVar) {
            super(2, dVar);
            this.$touchEvent = touchEvent;
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return DynamicIslandWindowView.this.new C09031(this.$touchEvent, dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            Object objC = Q0.c.c();
            int i3 = this.label;
            if (i3 == 0) {
                L0.o.b(obj);
                t tVar = DynamicIslandWindowView.this._onTouchEvent;
                TouchEvent touchEvent = this.$touchEvent;
                this.label = 1;
                if (tVar.emit(touchEvent, this) == objC) {
                    return objC;
                }
            } else {
                if (i3 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                L0.o.b(obj);
            }
            return w.f1623a;
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((C09031) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    @R0.f(c = "miui.systemui.dynamicisland.window.DynamicIslandWindowView$updateViewStateWhenCloseEnd$1", f = "DynamicIslandWindowView.kt", l = {}, m = "invokeSuspend")
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0002\u001a\u00020\u0001*\u00020\u0000H\u008a@¢\u0006\u0004\b\u0002\u0010\u0003"}, d2 = {"Lv2/E;", "LL0/w;", "<anonymous>", "(Lv2/E;)V"}, k = 3, mv = {1, 9, 0})
    /* renamed from: miui.systemui.dynamicisland.window.DynamicIslandWindowView$updateViewStateWhenCloseEnd$1, reason: invalid class name and case insensitive filesystem */
    public static final class C09041 extends R0.l implements Z0.o {
        final /* synthetic */ boolean $isFreeform;
        final /* synthetic */ DynamicIslandContentView $state;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C09041(DynamicIslandContentView dynamicIslandContentView, boolean z3, P0.d dVar) {
            super(2, dVar);
            this.$state = dynamicIslandContentView;
            this.$isFreeform = z3;
        }

        @Override // R0.a
        public final P0.d create(Object obj, P0.d dVar) {
            return new C09041(this.$state, this.$isFreeform, dVar);
        }

        @Override // R0.a
        public final Object invokeSuspend(Object obj) throws Throwable {
            DynamicIslandContentFakeView fakeView;
            Q0.c.c();
            if (this.label != 0) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            L0.o.b(obj);
            DynamicIslandContentView dynamicIslandContentView = this.$state;
            if (dynamicIslandContentView != null) {
                dynamicIslandContentView.updateViewStateWhenCloseEnd();
            }
            DynamicIslandContentView dynamicIslandContentView2 = this.$state;
            if (dynamicIslandContentView2 != null && (fakeView = dynamicIslandContentView2.getFakeView()) != null) {
                fakeView.updateViewStateWhenCloseEnd(this.$isFreeform);
            }
            return w.f1623a;
        }

        @Override // Z0.o
        public final Object invoke(E e3, P0.d dVar) {
            return ((C09041) create(e3, dVar)).invokeSuspend(w.f1623a);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DynamicIslandWindowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        kotlin.jvm.internal.o.g(context, "context");
        this.windowViewController = L0.h.b(new DynamicIslandWindowView$windowViewController$2(this));
        this.contentViewList = new CopyOnWriteArrayList<>();
        this.fakeViewList = new CopyOnWriteArrayList<>();
        this.dynamicIslandDataList = new ArrayList();
        this.currentUserId = ReflectBuilderUtil.getCurrentUserId();
        t tVarB = A.b(0, 0, null, 7, null);
        this._dispatchDrawEvent = tVarB;
        this.dispatchDrawEvent = AbstractC1235h.a(tVarB);
        this.lifecycle = L0.h.b(new DynamicIslandWindowView$lifecycle$2(this));
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miui.systemui.dynamicisland.window.m
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                DynamicIslandWindowView.onGlobalLayoutListener$lambda$1(this.f8453a);
            }
        };
        this.onGlobalLayoutListener = onGlobalLayoutListener;
        getLifecycle().setCurrentState(Lifecycle.State.CREATED);
        getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        this.previousConfig = new Configuration(getResources().getConfiguration());
        updateBlurContainer(MiBlurCompat.getBackgroundBlurOpened(context));
        t tVarB2 = A.b(0, 0, null, 7, null);
        this._onInterceptTouchEvent = tVarB2;
        t tVarB3 = A.b(0, 0, null, 7, null);
        this._onTouchEvent = tVarB3;
        this.onInterceptTouchEvent = AbstractC1235h.a(tVarB2);
        this.onTouchEvent = AbstractC1235h.a(tVarB3);
    }

    public static /* synthetic */ void addDynamicIslandData$default(DynamicIslandWindowView dynamicIslandWindowView, DynamicIslandData dynamicIslandData, boolean z3, float f3, float f4, boolean z4, int i3, Object obj) throws PendingIntent.CanceledException {
        if ((i3 & 16) != 0) {
            z4 = true;
        }
        dynamicIslandWindowView.addDynamicIslandData(dynamicIslandData, z3, f3, f4, z4);
    }

    private final boolean assertUserSpace(DynamicIslandData dynamicIslandData) {
        Bundle extras;
        Integer numValueOf = (dynamicIslandData == null || (extras = dynamicIslandData.getExtras()) == null) ? null : Integer.valueOf(extras.getInt("miui.user.id"));
        int i3 = this.currentUserId;
        boolean z3 = true;
        if (i3 != 0 ? !(numValueOf != null && numValueOf.intValue() == i3) : !((numValueOf != null && numValueOf.intValue() == 0) || (numValueOf != null && numValueOf.intValue() == 999))) {
            z3 = false;
        }
        Log.e(TAG, "assert: currentUserId=" + this.currentUserId + ", appUid=" + numValueOf + ", result=" + z3);
        return z3;
    }

    private final DynamicIslandContentView buildDynamicIslandContentView(DynamicIslandData dynamicIslandData, float maxWidth, float cutoutY) {
        View viewInflate = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_island_view, (ViewGroup) this, false);
        kotlin.jvm.internal.o.e(viewInflate, "null cannot be cast to non-null type miui.systemui.dynamicisland.DynamicIslandBackgroundView");
        DynamicIslandBackgroundView dynamicIslandBackgroundView = (DynamicIslandBackgroundView) viewInflate;
        DynamicIslandContentView dynamicIslandContentView = (DynamicIslandContentView) dynamicIslandBackgroundView.requireViewById(R.id.island_content);
        DynamicIslandContentViewController.Factory contentViewControllerFactory = getViewComponent().getContentViewControllerFactory();
        kotlin.jvm.internal.o.d(dynamicIslandContentView);
        dynamicIslandContentView.setController(contentViewControllerFactory.create(dynamicIslandContentView));
        DynamicIslandUtils dynamicIslandUtils = DynamicIslandUtils.INSTANCE;
        kotlin.jvm.internal.o.f(getContext(), "getContext(...)");
        dynamicIslandContentView.setTranslationX(dynamicIslandUtils.getScreenWidthOld(r1) / 2.0f);
        kotlin.jvm.internal.o.f(getContext(), "getContext(...)");
        dynamicIslandContentView.setTranslationY(-dynamicIslandUtils.getScreenHeightOld(r1));
        dynamicIslandContentView.setBackgroundView(dynamicIslandBackgroundView);
        addView(dynamicIslandBackgroundView);
        CopyOnWriteArrayList<DynamicIslandContentView> copyOnWriteArrayList = this.contentViewList;
        final AnonymousClass1 anonymousClass1 = new AnonymousClass1(dynamicIslandData, this);
        copyOnWriteArrayList.removeIf(new Predicate() { // from class: miui.systemui.dynamicisland.window.l
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return DynamicIslandWindowView.buildDynamicIslandContentView$lambda$18(anonymousClass1, obj);
            }
        });
        this.contentViewList.add(dynamicIslandContentView);
        onDynamicIslandDataChanged();
        DynamicIslandBaseContentViewBinder.INSTANCE.bind(dynamicIslandContentView, getViewComponent().getSizeRepository());
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        kotlin.jvm.internal.o.d(dynamicIslandEventCoordinator);
        dynamicIslandContentView.setEventHandler(dynamicIslandEventCoordinator);
        if (DynamicIslandBaseContentView.updateView$default(dynamicIslandContentView, dynamicIslandData, false, false, 4, null)) {
            return dynamicIslandContentView;
        }
        this.contentViewList.remove(dynamicIslandContentView);
        removeView(dynamicIslandBackgroundView);
        dynamicIslandContentView.getController().destroy();
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean buildDynamicIslandContentView$lambda$18(Function1 tmp0, Object obj) {
        kotlin.jvm.internal.o.g(tmp0, "$tmp0");
        return ((Boolean) tmp0.invoke(obj)).booleanValue();
    }

    private final DynamicIslandContentFakeView buildDynamicIslandFakeContentView(DynamicIslandData dynamicIslandData, float maxWidth, float cutoutY) {
        View viewInflate = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_island_fake_content_view, (ViewGroup) this, false);
        kotlin.jvm.internal.o.e(viewInflate, "null cannot be cast to non-null type miui.systemui.dynamicisland.window.content.DynamicIslandContentFakeView");
        DynamicIslandContentFakeView dynamicIslandContentFakeView = (DynamicIslandContentFakeView) viewInflate;
        addView(dynamicIslandContentFakeView);
        dynamicIslandContentFakeView.setController(getViewComponent().getBaseContentViewControllerFactory().create(dynamicIslandContentFakeView));
        this.fakeViewList.add(dynamicIslandContentFakeView);
        DynamicIslandBaseContentViewBinder.INSTANCE.bind(dynamicIslandContentFakeView, getViewComponent().getSizeRepository());
        DynamicIslandBaseContentView.updateView$default(dynamicIslandContentFakeView, dynamicIslandData, false, false, 4, null);
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        kotlin.jvm.internal.o.d(dynamicIslandEventCoordinator);
        dynamicIslandContentFakeView.setEventHandler(dynamicIslandEventCoordinator);
        return dynamicIslandContentFakeView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean canUpdate(DynamicIslandContentView update) {
        DynamicIslandContentFakeView fakeView;
        return (update == null || (fakeView = update.getFakeView()) == null || !fakeView.getMTrackingToOpenMW()) && !((Boolean) getWindowViewController().getIsFreeformAnimRunning().getValue()).booleanValue();
    }

    public static /* synthetic */ void clearAfterDelete$default(DynamicIslandWindowView dynamicIslandWindowView, DynamicIslandData dynamicIslandData, String str, boolean z3, int i3, Object obj) {
        if ((i3 & 4) != 0) {
            z3 = true;
        }
        dynamicIslandWindowView.clearAfterDelete(dynamicIslandData, str, z3);
    }

    private final boolean compareConfigurations(Configuration previousConfig, Configuration newConfig) {
        if (newConfig != null) {
            int i3 = previousConfig.orientation;
        }
        kotlin.jvm.internal.o.c(previousConfig.locale, newConfig != null ? newConfig.locale : null);
        kotlin.jvm.internal.o.a(previousConfig.fontScale, newConfig != null ? Float.valueOf(newConfig.fontScale) : null);
        return newConfig == null || previousConfig.getLayoutDirection() != newConfig.getLayoutDirection() || newConfig == null || previousConfig.densityDpi != newConfig.densityDpi;
    }

    public static /* synthetic */ int getMainAppExpandedTopLeveCount$default(DynamicIslandWindowView dynamicIslandWindowView, boolean z3, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            z3 = true;
        }
        return dynamicIslandWindowView.getMainAppExpandedTopLeveCount(z3);
    }

    private final int getWindowInsetsRotation(WindowInsets insets) {
        DisplayCutout displayCutout;
        if (insets == null || (displayCutout = insets.getDisplayCutout()) == null) {
            return -1;
        }
        if (displayCutout.getSafeInsetRight() > 0) {
            return 0;
        }
        if (displayCutout.getSafeInsetTop() > 0) {
            return 1;
        }
        if (displayCutout.getSafeInsetLeft() > 0) {
            return 2;
        }
        return displayCutout.getSafeInsetBottom() > 0 ? 3 : -1;
    }

    private final boolean hasDeviceNotification() {
        Integer properties;
        List<DynamicIslandData> list = this.dynamicIslandDataList;
        if (list != null && list.isEmpty()) {
            return false;
        }
        for (DynamicIslandData dynamicIslandData : list) {
            if (dynamicIslandData != null && (properties = dynamicIslandData.getProperties()) != null && properties.intValue() == 0) {
                return true;
            }
        }
        return false;
    }

    private final void initEventCoordinator() {
        this.eventCoordinator = getViewComponent().getDynamicIslandEventCoordinator();
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new C09011(null), 3, null);
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new AnonymousClass2(null), 3, null);
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new AnonymousClass3(null), 3, null);
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new AnonymousClass4(null), 3, null);
    }

    private final boolean isTinyScreen() {
        return getWindowViewController().getWindowState().getIsTinyScreen();
    }

    private final boolean isTouchInsideRect(float touchX, float touchY, Rect rect) {
        return rect != null && touchX >= ((float) rect.left) && touchX <= ((float) rect.right) && touchY >= ((float) rect.top) && touchY <= ((float) rect.bottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void maybeCollapseExpand$lambda$11(DynamicIslandWindowView this$0) throws PendingIntent.CanceledException {
        kotlin.jvm.internal.o.g(this$0, "this$0");
        this$0.collapse("input monitor");
    }

    private final void onDeviceNotificationChanged(boolean hasDeviceNotification) {
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener;
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_ISLAND_DEVICE_NOTIFICATION_CHANGED);
        bundle.putBoolean(DynamicIslandConstants.EXTRA_DEVICE_NOTIFICATION_ADD, hasDeviceNotification);
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (dynamicIslandViewChangedListener = dynamicIslandEventCoordinator.getDynamicIslandViewChangedListener()) == null) {
            return;
        }
        dynamicIslandViewChangedListener.onIslandViewChanged(bundle);
    }

    private final void onDynamicIslandDataChanged() {
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener;
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_ISLAND_DATA_CHANGED);
        bundle.putInt(DynamicIslandConstants.EXTRA_DATA_SIZE, this.contentViewList.size());
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (dynamicIslandViewChangedListener = dynamicIslandEventCoordinator.getDynamicIslandViewChangedListener()) == null) {
            return;
        }
        dynamicIslandViewChangedListener.onIslandViewChanged(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onGlobalLayoutListener$lambda$1(DynamicIslandWindowView this$0) {
        kotlin.jvm.internal.o.g(this$0, "this$0");
        this$0.getWindowViewController().setCurrentHeight(this$0.getHeight());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onLongPress$lambda$32(DynamicIslandBaseContentView view, DynamicIslandContentView dynamicIslandContentView, float f3) {
        kotlin.jvm.internal.o.g(view, "$view");
        DynamicIslandBaseContentViewController<?> dynamicIslandBaseContentViewController = view.get_controller();
        if (dynamicIslandBaseContentViewController != null) {
            dynamicIslandBaseContentViewController.onLongPressed(view, dynamicIslandContentView != null ? dynamicIslandContentView.getCurrentIslandData() : null, f3);
        }
    }

    public static /* synthetic */ void removeDynamicIslandData$default(DynamicIslandWindowView dynamicIslandWindowView, DynamicIslandData dynamicIslandData, boolean z3, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            z3 = false;
        }
        dynamicIslandWindowView.removeDynamicIslandData(dynamicIslandData, z3);
    }

    private final void removeTempShowBigIslandOnFlipTinyChanged() {
        if (CommonUtils.isFlipDevice()) {
            if (getWindowViewController().getWindowState().getIsTinyScreen() == getWindowViewController().getWindowState().getLastTinyScreenStatus() && getWindowViewController().getLastDisplayOrientation() == getWindowViewController().getDisplayOrientation()) {
                return;
            }
            isTempShowBigIslandToBeRemoved();
        }
    }

    private final Float requestCutoutY() throws Resources.NotFoundException {
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_BACK_REQUEST_CUTOUT_Y);
        float dimension = getContext().getResources().getDimension(R.dimen.island_height);
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = this.listener;
        Bundle bundleOnIslandViewChanged = dynamicIslandViewChangedListener != null ? dynamicIslandViewChangedListener.onIslandViewChanged(bundle) : null;
        if (bundleOnIslandViewChanged == null) {
            return null;
        }
        float tinyScreenInsetTop = bundleOnIslandViewChanged.getFloat(DynamicIslandConstants.EXTRA_BACK_REQUEST_CUTOUT_Y);
        if (FlipUtils.isFlipTiny()) {
            tinyScreenInsetTop += DynamicIslandUtils.INSTANCE.getTinyScreenInsetTop();
        }
        Log.e(TAG, "requestCutoutY " + tinyScreenInsetTop);
        return CommonUtils.isNotchScreenDevice() ? Float.valueOf(dimension / 2) : Float.valueOf(tinyScreenInsetTop);
    }

    private final void requestImmersiveMode() {
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_BACK_REQUEST_IMMERSIVE_MODE);
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = this.listener;
        Bundle bundleOnIslandViewChanged = dynamicIslandViewChangedListener != null ? dynamicIslandViewChangedListener.onIslandViewChanged(bundle) : null;
        if (bundleOnIslandViewChanged != null) {
            Log.e(TAG, "requestImmersiveMode " + bundleOnIslandViewChanged.getBoolean(DynamicIslandConstants.EXTRA_BACK_REQUEST_IMMERSIVE_MODE));
        }
    }

    private final Float requestMaxIslandWidth() {
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_BACK_REQUEST_MAX_WIDTH);
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = this.listener;
        Bundle bundleOnIslandViewChanged = dynamicIslandViewChangedListener != null ? dynamicIslandViewChangedListener.onIslandViewChanged(bundle) : null;
        if (bundleOnIslandViewChanged == null) {
            return null;
        }
        float f3 = bundleOnIslandViewChanged.getFloat(DynamicIslandConstants.EXTRA_ISLAND_MAX_WIDTH);
        Log.e(TAG, "requestMaxIslandWidth " + f3);
        return Float.valueOf(f3);
    }

    private final void sendExitPendingIntent(Bundle extras) throws PendingIntent.CanceledException {
        if (extras == null || !extras.containsKey("miui.exitFloating")) {
            return;
        }
        Log.e(TAG, "sendExitPendingIntent: ");
        Parcelable parcelable = extras.getParcelable("miui.exitFloating");
        if (parcelable instanceof PendingIntent) {
            ((PendingIntent) parcelable).send();
        }
    }

    private final void updateBlurContainer(boolean supportBLur) {
        MiBlurCompat.setMiBackgroundBlurModeCompat(this, supportBLur ? 1 : 0);
        MiBlurCompat.setMiBackgroundBlurRadiusCompat(this, supportBLur ? 100 : 0);
        MiBlurCompat.setPassWindowBlurEnabledCompat(this, supportBLur);
    }

    private final void updateDynamicIslandDataList(DynamicIslandData dynamicIslandData) {
        boolean z3;
        Iterator<DynamicIslandData> it = this.dynamicIslandDataList.iterator();
        while (true) {
            if (!it.hasNext()) {
                z3 = false;
                break;
            } else if (kotlin.jvm.internal.o.c(it.next().getKey(), dynamicIslandData.getKey())) {
                it.remove();
                z3 = true;
                break;
            }
        }
        Log.e(TAG, "updateDynamicIslandDataList: " + dynamicIslandData.getKey() + "   " + z3);
        if (z3) {
            this.dynamicIslandDataList.add(dynamicIslandData);
        }
        onDeviceNotificationChanged(hasDeviceNotification());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateExpandedView(DynamicIslandBaseContentView view, DynamicIslandData dynamicIslandData, Integer height) throws Resources.NotFoundException {
        View view2;
        DynamicIslandExpandedView expandedView;
        DynamicIslandExpandedView expandedView2;
        View view3;
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.expanded_min_height);
        Log.d(TAG, "height " + height + " " + ((dynamicIslandData == null || (view3 = dynamicIslandData.getView()) == null) ? null : Integer.valueOf(view3.getHeight())) + " " + dimensionPixelSize);
        int iIntValue = height != null ? height.intValue() : (dynamicIslandData == null || (view2 = dynamicIslandData.getView()) == null) ? 0 : view2.getHeight();
        if ((dynamicIslandData != null ? dynamicIslandData.getView() : null) != null) {
            if (isMediaApp(dynamicIslandData)) {
                if (view != null) {
                    view.updateExpandedSize(view.getExpandedViewMaxWidth(), view.getExpandedViewMaxHeight(), dynamicIslandData);
                    return;
                }
                return;
            }
            if ((iIntValue > 0 ? iIntValue : dimensionPixelSize) < dimensionPixelSize) {
                if (view == null || (expandedView2 = view.getExpandedView()) == null) {
                    return;
                }
                view.updateExpandedSize(expandedView2.getWidth(), dimensionPixelSize, dynamicIslandData);
                return;
            }
            if (view == null || (expandedView = view.getExpandedView()) == null) {
                return;
            }
            view.updateExpandedSize(expandedView.getWidth(), iIntValue, dynamicIslandData);
        }
    }

    public static /* synthetic */ void updateExpandedView$default(DynamicIslandWindowView dynamicIslandWindowView, DynamicIslandBaseContentView dynamicIslandBaseContentView, DynamicIslandData dynamicIslandData, Integer num, int i3, Object obj) throws Resources.NotFoundException {
        if ((i3 & 4) != 0) {
            num = null;
        }
        dynamicIslandWindowView.updateExpandedView(dynamicIslandBaseContentView, dynamicIslandData, num);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public final void addDynamicIslandData(final DynamicIslandData dynamicIslandData, boolean expanded, float maxWidth, float cutoutY, boolean addToHistoryList) throws PendingIntent.CanceledException {
        kotlin.jvm.internal.o.g(dynamicIslandData, "dynamicIslandData");
        Log.d(TAG, "addDynamicIslandData cutoutY=" + cutoutY);
        if (assertUserSpace(dynamicIslandData)) {
            if (addToHistoryList) {
                this.dynamicIslandDataList.add(dynamicIslandData);
                onDeviceNotificationChanged(hasDeviceNotification());
            }
            Bundle extras = dynamicIslandData.getExtras();
            String string = extras != null ? extras.getString("miui.pkg.name") : null;
            String key = dynamicIslandData.getKey();
            DynamicIslandContentView viewFromList = key != null ? getViewFromList(key) : null;
            if ((viewFromList != null ? viewFromList.getState() : null) != null) {
                updateDynamicIslandView(dynamicIslandData, expanded, maxWidth);
                return;
            }
            Bundle extras2 = dynamicIslandData.getExtras();
            notifyAddIsland(string, extras2 != null ? Integer.valueOf(extras2.getInt("miui.user.id")) : null, dynamicIslandData.getKey(), dynamicIslandData.getProperties());
            requestImmersiveMode();
            final DynamicIslandContentView dynamicIslandContentViewBuildDynamicIslandContentView = buildDynamicIslandContentView(dynamicIslandData, maxWidth, cutoutY);
            if (dynamicIslandContentViewBuildDynamicIslandContentView == null) {
                return;
            }
            DynamicIslandContentFakeView dynamicIslandContentFakeViewBuildDynamicIslandFakeContentView = buildDynamicIslandFakeContentView(dynamicIslandData, maxWidth, cutoutY);
            DynamicIslandState.Init init = new DynamicIslandState.Init();
            init.setTime(Long.valueOf(System.currentTimeMillis()));
            Integer properties = dynamicIslandData.getProperties();
            init.setTempShow(properties != null && properties.intValue() == 0);
            init.setExpanded(expanded);
            dynamicIslandContentViewBuildDynamicIslandContentView.setState(init);
            dynamicIslandContentViewBuildDynamicIslandContentView.setFakeView(dynamicIslandContentFakeViewBuildDynamicIslandFakeContentView);
            ViewGroup viewGroup = this.mGlowEffectTopContainer;
            if (viewGroup == null) {
                kotlin.jvm.internal.o.w("mGlowEffectTopContainer");
                viewGroup = null;
            }
            ViewGroup viewGroup2 = this.mGlowEffectBottomContainer;
            if (viewGroup2 == null) {
                kotlin.jvm.internal.o.w("mGlowEffectBottomContainer");
                viewGroup2 = null;
            }
            dynamicIslandContentViewBuildDynamicIslandContentView.initGlowEffect$miui_dynamicisland_release(viewGroup, viewGroup2);
            dynamicIslandContentFakeViewBuildDynamicIslandFakeContentView.setRealView(dynamicIslandContentViewBuildDynamicIslandContentView);
            dynamicIslandContentViewBuildDynamicIslandContentView.getViewModel().setState(null, init);
            getWindowViewController().updateChronometersIn(dynamicIslandContentViewBuildDynamicIslandContentView, dynamicIslandContentFakeViewBuildDynamicIslandFakeContentView, dynamicIslandData);
            getWindowViewController().addLottieAnimView(dynamicIslandContentViewBuildDynamicIslandContentView, dynamicIslandContentFakeViewBuildDynamicIslandFakeContentView, dynamicIslandData.getKey());
            if (canExpanded(expanded, dynamicIslandData.getView())) {
                DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
                if (dynamicIslandEventCoordinator != null) {
                    dynamicIslandEventCoordinator.onHeightChangedFirst();
                }
                DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
                if (dynamicIslandEventCoordinator2 != null) {
                    dynamicIslandEventCoordinator2.setUserExpanded(false);
                }
            }
            OneShotPreDrawListener.add(this, new Runnable() { // from class: miui.systemui.dynamicisland.window.DynamicIslandWindowView$addDynamicIslandData$$inlined$doOnPreDraw$1
                @Override // java.lang.Runnable
                public final void run() {
                    if (this.getContentViewList().contains(dynamicIslandContentViewBuildDynamicIslandContentView)) {
                        AbstractC1178g.b(this.getWindowViewController().getIoScope(), null, null, new DynamicIslandWindowView$addDynamicIslandData$1$1(this, dynamicIslandContentViewBuildDynamicIslandContentView, dynamicIslandData, null), 3, null);
                    }
                }
            });
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator3 = this.eventCoordinator;
            if (dynamicIslandEventCoordinator3 != null) {
                dynamicIslandEventCoordinator3.updateFreeformFakeView(dynamicIslandContentFakeViewBuildDynamicIslandFakeContentView, dynamicIslandContentViewBuildDynamicIslandContentView, dynamicIslandData.getExtras());
            }
        }
    }

    public final void addOnDynamicIslandViewChangedListener(DynamicIslandContent.DynamicIslandViewChangedListener listener) {
        kotlin.jvm.internal.o.g(listener, "listener");
        this.listener = listener;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.addDynamicIslandViewChangedListener(listener);
        }
    }

    @Override // android.view.ViewGroup
    @CallSuper
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childCount;
        kotlin.jvm.internal.o.g(child, "child");
        kotlin.jvm.internal.o.g(params, "params");
        ViewGroup viewGroup = this.mGlowEffectTopContainer;
        if (viewGroup != null) {
            if (viewGroup == null) {
                kotlin.jvm.internal.o.w("mGlowEffectTopContainer");
                viewGroup = null;
            }
            childCount = indexOfChild(viewGroup);
        } else {
            childCount = getChildCount();
        }
        if (index >= 0) {
            childCount = Math.min(childCount, index);
        }
        super.addView(child, childCount, params);
    }

    public final void appEnter(DynamicIslandContentView state) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator;
        if (state == null || (dynamicIslandEventCoordinator = this.eventCoordinator) == null) {
            return;
        }
        dynamicIslandEventCoordinator.dispatchEvent(DynamicIslandEvent.EnterApp.INSTANCE, state);
    }

    public final void appExit(DynamicIslandContentView view) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator;
        if (view == null || (dynamicIslandEventCoordinator = this.eventCoordinator) == null) {
            return;
        }
        dynamicIslandEventCoordinator.dispatchEvent(DynamicIslandEvent.ExitApp.INSTANCE, view);
    }

    public final DynamicIslandState calculateCollapse(DynamicIslandContentView state) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            return dynamicIslandEventCoordinator.calculateCollapse(state);
        }
        return null;
    }

    public final boolean canExpanded(boolean expanded, View expandedView) {
        return expanded && !((Boolean) getWindowViewController().getWindowState().getNotificationAppearance().getValue()).booleanValue() && (!((Boolean) getWindowViewController().getWindowState().getScreenLocked().getValue()).booleanValue() || ((Boolean) getWindowViewController().getWindowState().getControlCenterExpanded().getValue()).booleanValue()) && expandedView != null;
    }

    public final void cancelExpandViewTrackingAnim(DynamicIslandContentView state) {
        DynamicIslandContentFakeView fakeView;
        if (state == null || (fakeView = state.getFakeView()) == null) {
            return;
        }
        fakeView.cancelExpandViewTrackingAnim();
    }

    public final void cancelLongPressRunnable(DynamicIslandBaseContentView view) {
        kotlin.jvm.internal.o.g(view, "view");
        Runnable runnable = this.longPress;
        if (runnable != null) {
            view.removeCallbacks(runnable);
            this.longPress = null;
        }
    }

    public final void clearAfterDelete(DynamicIslandData dynamicIslandData, String key, boolean removeFromList) {
        Bundle extras;
        Bundle extras2;
        notifyRemoveIsland((dynamicIslandData == null || (extras2 = dynamicIslandData.getExtras()) == null) ? null : extras2.getString("miui.pkg.name"), (dynamicIslandData == null || (extras = dynamicIslandData.getExtras()) == null) ? null : Integer.valueOf(extras.getInt("miui.user.id")), key, dynamicIslandData != null ? dynamicIslandData.getProperties() : null);
        if (key != null) {
            getViewComponent().getIslandTemplateFactory().removeTemplate(key);
        }
        for (DynamicIslandContentView dynamicIslandContentView : this.contentViewList) {
            DynamicIslandData currentIslandData = dynamicIslandContentView.getCurrentIslandData();
            if (kotlin.jvm.internal.o.c(currentIslandData != null ? currentIslandData.getKey() : null, key)) {
                this.contentViewList.remove(dynamicIslandContentView);
                onDynamicIslandDataChanged();
            }
        }
        if (removeFromList) {
            Iterator<DynamicIslandData> it = this.dynamicIslandDataList.iterator();
            while (it.hasNext()) {
                if (TextUtils.equals(it.next().getKey(), key)) {
                    it.remove();
                }
            }
        }
        onDeviceNotificationChanged(hasDeviceNotification());
        for (DynamicIslandContentFakeView dynamicIslandContentFakeView : this.fakeViewList) {
            DynamicIslandData currentIslandData2 = dynamicIslandContentFakeView.getCurrentIslandData();
            if (kotlin.jvm.internal.o.c(currentIslandData2 != null ? currentIslandData2.getKey() : null, key)) {
                this.fakeViewList.remove(dynamicIslandContentFakeView);
                removeView(dynamicIslandContentFakeView);
                dynamicIslandContentFakeView.getController().destroy();
            }
        }
    }

    public final void collapse(String reason) throws PendingIntent.CanceledException {
        DynamicIslandData currentIslandData;
        DynamicIslandContentFakeView fakeView;
        kotlin.jvm.internal.o.g(reason, "reason");
        DynamicIslandContentView currentExpandedState = getCurrentExpandedState();
        boolean z3 = (currentExpandedState == null || (fakeView = currentExpandedState.getFakeView()) == null || !fakeView.getOpenAppFromIsland()) ? false : true;
        DynamicIslandContentView currentExpandedState2 = getCurrentExpandedState();
        boolean z4 = currentExpandedState2 != null && currentExpandedState2.getOpenAppFromIsland();
        Log.d(TAG, "skip collapse=(" + z4 + "||" + z3 + "), reason=" + reason);
        if (z4 || z3) {
            return;
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.setUserExpanded(false);
        }
        DynamicIslandContentView currentExpandedState3 = getCurrentExpandedState();
        if (currentExpandedState3 != null && (currentIslandData = currentExpandedState3.getCurrentIslandData()) != null) {
            sendExitPendingIntent(currentIslandData.getExtras());
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
        if (dynamicIslandEventCoordinator2 != null) {
            DynamicIslandEventCoordinator.dispatchEvent$default(dynamicIslandEventCoordinator2, DynamicIslandEvent.Collapse.INSTANCE, null, 2, null);
        }
    }

    public final void destroy() {
        getLifecycle().setCurrentState(Lifecycle.State.DESTROYED);
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        getWindowViewController().destroy();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        kotlin.jvm.internal.o.g(canvas, "canvas");
        super.dispatchDraw(canvas);
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new C09001(canvas, null), 3, null);
    }

    public final void enterMiniWindow(DynamicIslandContentView state) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator;
        if (state == null || (dynamicIslandEventCoordinator = this.eventCoordinator) == null) {
            return;
        }
        dynamicIslandEventCoordinator.dispatchEvent(DynamicIslandEvent.EnterMiniWindow.INSTANCE, state);
    }

    public final void enterMiniWindowEnd() {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null) {
            return;
        }
        dynamicIslandEventCoordinator.setEnterMiniWindow(false);
    }

    public final void exitMiniWindow(DynamicIslandContentView state) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator;
        if (state == null || (dynamicIslandEventCoordinator = this.eventCoordinator) == null) {
            return;
        }
        dynamicIslandEventCoordinator.dispatchEvent(DynamicIslandEvent.ExitMiniWindow.INSTANCE, state);
    }

    public final CopyOnWriteArrayList<DynamicIslandContentView> getContentViewList() {
        return this.contentViewList;
    }

    public final List<DynamicIslandContentView> getCurrentAppExpandedState() {
        AppStateHandler appStateHandler;
        ArrayList<DynamicIslandContentView> currentList;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        return (dynamicIslandEventCoordinator == null || (appStateHandler = dynamicIslandEventCoordinator.getAppStateHandler()) == null || (currentList = appStateHandler.getCurrentList()) == null) ? new ArrayList() : currentList;
    }

    public final DynamicIslandContentView getCurrentBigIslandState() {
        BigIslandStateHandler bigIslandStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (bigIslandStateHandler = dynamicIslandEventCoordinator.getBigIslandStateHandler()) == null) {
            return null;
        }
        return bigIslandStateHandler.getCurrent();
    }

    public final DynamicIslandContentView getCurrentExpandedState() {
        ExpandedStateHandler expandedStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (expandedStateHandler = dynamicIslandEventCoordinator.getExpandedStateHandler()) == null) {
            return null;
        }
        return expandedStateHandler.getCurrent();
    }

    public final List<DynamicIslandContentView> getCurrentMiniWindowState(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        HashMap<String, ArrayList<DynamicIslandContentView>> currentMap;
        ArrayList<DynamicIslandContentView> arrayList;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        ArrayList arrayList2 = new ArrayList();
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null && (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) != null && (currentMap = miniWindowStateHandler.getCurrentMap()) != null && (arrayList = currentMap.get(pkg)) != null) {
            arrayList2.addAll(arrayList);
        }
        return arrayList2;
    }

    public final DynamicIslandContentView getCurrentSmallIslandState() {
        SmallIslandStateHandler smallIslandStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (smallIslandStateHandler = dynamicIslandEventCoordinator.getSmallIslandStateHandler()) == null) {
            return null;
        }
        return smallIslandStateHandler.getCurrent();
    }

    public final DynamicIslandContentView getCurrentTempShowBigIslandState() {
        BigIslandStateHandler bigIslandStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (bigIslandStateHandler = dynamicIslandEventCoordinator.getBigIslandStateHandler()) == null) {
            return null;
        }
        return bigIslandStateHandler.getCurrentTempShow();
    }

    public final int getCutoutHeight() throws Resources.NotFoundException {
        return Math.min(getCutoutWidth(), (int) (getContext().getResources().getDimensionPixelSize(R.dimen.island_height) * 0.9f));
    }

    public final Rect getCutoutRect() {
        Rect rect = new Rect();
        DynamicIslandUtils dynamicIslandUtils = DynamicIslandUtils.INSTANCE;
        Context context = getContext();
        kotlin.jvm.internal.o.f(context, "getContext(...)");
        rect.left = (dynamicIslandUtils.getScreenWidthOld(context) / 2) - (getCutoutWidth() / 2);
        rect.top = ((int) getWindowViewController().getCutoutY()) - (getCutoutHeight() / 2);
        rect.right = rect.left + getCutoutWidth();
        rect.bottom = rect.top + getCutoutHeight();
        return rect;
    }

    public final int getCutoutWidth() {
        DynamicIslandUtils dynamicIslandUtils = DynamicIslandUtils.INSTANCE;
        Context context = getContext();
        kotlin.jvm.internal.o.f(context, "getContext(...)");
        int iDpToPx = dynamicIslandUtils.dpToPx(20, context);
        return (FoldUtils.INSTANCE.isFoldScreenLayoutLarge(this) || FlipUtils.isFlipTiny() || dynamicIslandUtils.getCutoutBoundingRectTopWidth() <= 0) ? iDpToPx : dynamicIslandUtils.getCutoutBoundingRectTopWidth();
    }

    public final y getDispatchDrawEvent() {
        return this.dispatchDrawEvent;
    }

    public final DynamicIslandEventCoordinator getEventCoordinator() {
        return this.eventCoordinator;
    }

    public final List<DynamicIslandContentFakeView> getFakeViews() {
        return x.K0(this.fakeViewList);
    }

    /* renamed from: getHeadsUpHeight, reason: from getter */
    public final int get_headsUpHeight() {
        return this._headsUpHeight;
    }

    public final DynamicIslandContentView getMainAppExpanded() {
        AppStateHandler appStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (appStateHandler = dynamicIslandEventCoordinator.getAppStateHandler()) == null) {
            return null;
        }
        return appStateHandler.getMainElement();
    }

    public final DynamicIslandContentView getMainAppExpandedTopLeve() {
        AppStateHandler appStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (appStateHandler = dynamicIslandEventCoordinator.getAppStateHandler()) == null) {
            return null;
        }
        return appStateHandler.getTopLevel();
    }

    public final int getMainAppExpandedTopLeveCount(boolean bigIsland) {
        AppStateHandler appStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (appStateHandler = dynamicIslandEventCoordinator.getAppStateHandler()) == null) {
            return 0;
        }
        return appStateHandler.getTopLevelCount(bigIsland);
    }

    public final DynamicIslandContentView getMainMiniWindow(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) == null) {
            return null;
        }
        return miniWindowStateHandler.getMainElement(pkg);
    }

    public final DynamicIslandContentView getMainMiniWindowState(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) == null) {
            return null;
        }
        return miniWindowStateHandler.getMainElement(pkg);
    }

    public final DynamicIslandContentView getMainMiniWindowTopLeve(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) == null) {
            return null;
        }
        return miniWindowStateHandler.getTopLevel(pkg);
    }

    public final int getMainMiniWindowTopLeveCount(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) == null) {
            return 0;
        }
        return miniWindowStateHandler.getTopLevelCount(pkg);
    }

    public final DynamicIslandContentFakeView getMiniWindowContentFakeView(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        DynamicIslandContentView mainElement;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) == null || (mainElement = miniWindowStateHandler.getMainElement(pkg)) == null) {
            return null;
        }
        return mainElement.getFakeView();
    }

    public final y getOnInterceptTouchEvent() {
        return this.onInterceptTouchEvent;
    }

    public final y getOnTouchEvent() {
        return this.onTouchEvent;
    }

    public final DynamicIslandContentView getSubAppExpanded() {
        AppStateHandler appStateHandler;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator == null || (appStateHandler = dynamicIslandEventCoordinator.getAppStateHandler()) == null) {
            return null;
        }
        return appStateHandler.getSubElement();
    }

    public final DynamicIslandViewComponent getViewComponent() {
        DynamicIslandViewComponent dynamicIslandViewComponent = this._viewComponent;
        if (dynamicIslandViewComponent != null) {
            return dynamicIslandViewComponent;
        }
        throw new IllegalStateException("DynamicIslandViewComponent was not initialized.");
    }

    public final DynamicIslandContentView getViewFromList(String key) {
        Object obj;
        Iterator<T> it = this.contentViewList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            DynamicIslandData currentIslandData = ((DynamicIslandContentView) next).getCurrentIslandData();
            if (kotlin.jvm.internal.o.c(currentIslandData != null ? currentIslandData.getKey() : null, key)) {
                obj = next;
                break;
            }
        }
        return (DynamicIslandContentView) obj;
    }

    public final DynamicIslandWindowViewController getWindowViewController() {
        return (DynamicIslandWindowViewController) this.windowViewController.getValue();
    }

    public final boolean hasNoActiveDynamicIsland() {
        return this.dynamicIslandDataList.size() == 0;
    }

    public final boolean hasOtherBigIsland(DynamicIslandContentView view) {
        BigIslandStateHandler bigIslandStateHandler;
        DynamicIslandContentView current;
        DynamicIslandData currentIslandData;
        BigIslandStateHandler bigIslandStateHandler2;
        DynamicIslandContentView current2;
        DynamicIslandData currentIslandData2;
        BigIslandStateHandler bigIslandStateHandler3;
        DynamicIslandData currentIslandData3;
        Integer properties = null;
        String key = (view == null || (currentIslandData3 = view.getCurrentIslandData()) == null) ? null : currentIslandData3.getKey();
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (((dynamicIslandEventCoordinator == null || (bigIslandStateHandler3 = dynamicIslandEventCoordinator.getBigIslandStateHandler()) == null) ? null : bigIslandStateHandler3.getCurrent()) != null) {
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
            if (!kotlin.jvm.internal.o.c((dynamicIslandEventCoordinator2 == null || (bigIslandStateHandler2 = dynamicIslandEventCoordinator2.getBigIslandStateHandler()) == null || (current2 = bigIslandStateHandler2.getCurrent()) == null || (currentIslandData2 = current2.getCurrentIslandData()) == null) ? null : currentIslandData2.getKey(), key)) {
                DynamicIslandWindowState windowState = getWindowViewController().getWindowState();
                DynamicIslandEventCoordinator dynamicIslandEventCoordinator3 = this.eventCoordinator;
                if (dynamicIslandEventCoordinator3 != null && (bigIslandStateHandler = dynamicIslandEventCoordinator3.getBigIslandStateHandler()) != null && (current = bigIslandStateHandler.getCurrent()) != null && (currentIslandData = current.getCurrentIslandData()) != null) {
                    properties = currentIslandData.getProperties();
                }
                if (!windowState.isTempHidden(properties)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean hasSubAppExpanded(String pkg) {
        AppStateHandler appStateHandler;
        ArrayList<DynamicIslandContentView> currentList;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        return ((dynamicIslandEventCoordinator == null || (appStateHandler = dynamicIslandEventCoordinator.getAppStateHandler()) == null || (currentList = appStateHandler.getCurrentList()) == null) ? 0 : currentList.size()) > 1;
    }

    public final boolean hasSubMiniWindow(String pkg) {
        MiniWindowStateHandler miniWindowStateHandler;
        HashMap<String, ArrayList<DynamicIslandContentView>> currentMap;
        ArrayList<DynamicIslandContentView> arrayList;
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        return ((dynamicIslandEventCoordinator == null || (miniWindowStateHandler = dynamicIslandEventCoordinator.getMiniWindowStateHandler()) == null || (currentMap = miniWindowStateHandler.getCurrentMap()) == null || (arrayList = currentMap.get(pkg)) == null) ? 0 : arrayList.size()) > 1;
    }

    public final void hideAllElementSurface() {
        DynamicIslandData currentIslandData;
        DynamicIslandData currentIslandData2;
        Bundle extras;
        for (DynamicIslandContentView dynamicIslandContentView : this.contentViewList) {
            Bundle extras2 = null;
            Log.d(TAG, "hideAllElementSurface: " + ((dynamicIslandContentView == null || (currentIslandData2 = dynamicIslandContentView.getCurrentIslandData()) == null || (extras = currentIslandData2.getExtras()) == null) ? null : extras.getString("miui.pkg.name")));
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
            if (dynamicIslandEventCoordinator != null) {
                if (dynamicIslandContentView != null && (currentIslandData = dynamicIslandContentView.getCurrentIslandData()) != null) {
                    extras2 = currentIslandData.getExtras();
                }
                dynamicIslandEventCoordinator.alreadyCloseAppEnd(extras2);
            }
        }
    }

    public final boolean isDownInSeekBar(Context sysUIContext, float touchX, float touchY, View view) {
        View viewFindViewById;
        kotlin.jvm.internal.o.g(sysUIContext, "sysUIContext");
        if (view == null) {
            return false;
        }
        List<DynamicIslandData> list = this.dynamicIslandDataList;
        if (list != null && list.isEmpty()) {
            return false;
        }
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            if (isMediaApp((DynamicIslandData) it.next())) {
                Resources resources = sysUIContext.getResources();
                Integer numValueOf = resources != null ? Integer.valueOf(resources.getIdentifier("media_progress_bar", Column.ID, "com.android.systemui")) : null;
                Integer num = (numValueOf == null || numValueOf.intValue() != 0) ? numValueOf : null;
                if (num == null || (viewFindViewById = view.findViewById(num.intValue())) == null) {
                    return false;
                }
                try {
                    Rect rect = new Rect();
                    if (!viewFindViewById.getGlobalVisibleRect(rect) || touchX < rect.left || touchX > rect.right || touchY < rect.top) {
                        return false;
                    }
                    return touchY <= ((float) rect.bottom);
                } catch (Exception unused) {
                    return false;
                }
            }
        }
        return false;
    }

    /* renamed from: isLight, reason: from getter */
    public final boolean getIsLight() {
        return this.isLight;
    }

    public final boolean isMediaApp(DynamicIslandData dynamicIslandData) {
        Bundle extras;
        return ((dynamicIslandData == null || (extras = dynamicIslandData.getExtras()) == null) ? null : (PendingIntent) extras.getParcelable("miui.pending.intent", PendingIntent.class)) != null;
    }

    public final boolean isSwipeTowardsSmallIsland(float diffx) {
        Context context = getContext();
        kotlin.jvm.internal.o.f(context, "getContext(...)");
        if (CommonUtils.isLayoutRtl(context)) {
            if (diffx > 0.0f) {
                return false;
            }
        } else if (diffx < 0.0f) {
            return false;
        }
        return true;
    }

    public final boolean isTempShowBigIslandToBeRemoved() {
        DynamicIslandData currentIslandData;
        DynamicIslandContentView currentTempShowBigIslandState = getCurrentTempShowBigIslandState();
        if (currentTempShowBigIslandState == null || (currentIslandData = currentTempShowBigIslandState.getCurrentIslandData()) == null) {
            return false;
        }
        removeDynamicIslandData$default(this, currentIslandData, false, 2, null);
        return true;
    }

    public final boolean isUserExpanded() {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        return dynamicIslandEventCoordinator != null && dynamicIslandEventCoordinator.get_userExpanded();
    }

    public final void maybeCollapseExpand(int x3, int y3) {
        I touchRegion;
        Region region;
        I headsUpZone;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        L0.m mVar = (dynamicIslandEventCoordinator == null || (headsUpZone = dynamicIslandEventCoordinator.getHeadsUpZone()) == null) ? null : (L0.m) headsUpZone.getValue();
        if (mVar == null || y3 < ((Number) mVar.d()).intValue() || y3 > ((Number) mVar.e()).intValue()) {
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
            if ((dynamicIslandEventCoordinator2 == null || (touchRegion = dynamicIslandEventCoordinator2.getTouchRegion()) == null || (region = (Region) touchRegion.getValue()) == null || !region.contains(x3, y3)) && isUserExpanded()) {
                post(new Runnable() { // from class: miui.systemui.dynamicisland.window.k
                    @Override // java.lang.Runnable
                    public final void run() throws PendingIntent.CanceledException {
                        DynamicIslandWindowView.maybeCollapseExpand$lambda$11(this.f8451a);
                    }
                });
            }
        }
    }

    public final boolean needExtendLifetime(String key) {
        kotlin.jvm.internal.o.g(key, "key");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            return dynamicIslandEventCoordinator.needExtendLifetime(key);
        }
        return false;
    }

    public final void notifyAddIsland(String pkg, Integer uid, String key, Integer prop) {
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_BACK_ADD_ISLAND);
        bundle.putString(DynamicIslandConstants.EXTRA_BACK_ISLAND_PKG, pkg);
        if (uid != null) {
            bundle.putInt(DynamicIslandConstants.EXTRA_BACK_ISLAND_UID, uid.intValue());
        }
        bundle.putString(DynamicIslandConstants.EXTRA_BACK_ISLAND_KEY, key);
        if (prop != null) {
            bundle.putInt(DynamicIslandConstants.EXTRA_BACK_ISLAND_PROP, prop.intValue());
        }
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = this.listener;
        if (dynamicIslandViewChangedListener != null) {
            dynamicIslandViewChangedListener.onIslandViewChanged(bundle);
        }
    }

    public final Bundle notifyIslandViewChanged(Bundle bundle) {
        kotlin.jvm.internal.o.g(bundle, "bundle");
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = this.listener;
        if (dynamicIslandViewChangedListener != null) {
            return dynamicIslandViewChangedListener.onIslandViewChanged(bundle);
        }
        return null;
    }

    public final void notifyRemoveIsland(String pkg, Integer uid, String key, Integer prop) {
        Bundle bundle = new Bundle();
        bundle.putString(DynamicIslandConstants.ACTION_KEY, DynamicIslandConstants.ACTION_BACK_REMOVE_ISLAND);
        bundle.putString(DynamicIslandConstants.EXTRA_BACK_ISLAND_PKG, pkg);
        if (uid != null) {
            bundle.putInt(DynamicIslandConstants.EXTRA_BACK_ISLAND_UID, uid.intValue());
        }
        bundle.putString(DynamicIslandConstants.EXTRA_BACK_ISLAND_KEY, key);
        if (prop != null) {
            bundle.putInt(DynamicIslandConstants.EXTRA_BACK_ISLAND_PROP, prop.intValue());
        }
        DynamicIslandContent.DynamicIslandViewChangedListener dynamicIslandViewChangedListener = this.listener;
        if (dynamicIslandViewChangedListener != null) {
            dynamicIslandViewChangedListener.onIslandViewChanged(bundle);
        }
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) throws Resources.NotFoundException {
        DynamicIslandContentView tempShow;
        int windowInsetsRotation = getWindowInsetsRotation(insets);
        Log.d(TAG, "onApplyWindowInsets r=" + windowInsetsRotation);
        int displayOrientation = getWindowViewController().getDisplayOrientation();
        boolean z3 = false;
        if (displayOrientation == 0 ? windowInsetsRotation == 2 : !(displayOrientation == 1 ? windowInsetsRotation != 3 : displayOrientation == 2 ? windowInsetsRotation != 0 : displayOrientation != 3 || windowInsetsRotation != 1)) {
            z3 = true;
        }
        if (CommonUtils.isNotchScreenDevice() || (FlipUtils.isFlipTiny() && z3)) {
            getWindowViewController().updateWindowState();
            Float fRequestCutoutY = requestCutoutY();
            if (fRequestCutoutY != null) {
                getViewComponent().getSizeRepository().updateCutoutY(fRequestCutoutY.floatValue());
            }
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
            if (dynamicIslandEventCoordinator != null && (tempShow = dynamicIslandEventCoordinator.getTempShow()) != null && !tempShow.isAnimating()) {
                removeDynamicIslandData(tempShow.getCurrentIslandData(), true);
            }
        }
        WindowInsets windowInsetsOnApplyWindowInsets = super.onApplyWindowInsets(insets);
        kotlin.jvm.internal.o.f(windowInsetsOnApplyWindowInsets, "onApplyWindowInsets(...)");
        return windowInsetsOnApplyWindowInsets;
    }

    public final void onConfigChanged(Configuration newConfig) throws Resources.NotFoundException {
        DynamicIslandData currentIslandData;
        Integer properties;
        kotlin.jvm.internal.o.g(newConfig, "newConfig");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.updateTouchRegion();
        }
        Log.d(TAG, "isTiny=" + getWindowViewController().getWindowState().getIsTinyScreen() + " config changed to " + newConfig);
        getWindowViewController().getWindowState().setConfigChange(Boolean.TRUE);
        Float fRequestCutoutY = requestCutoutY();
        if (fRequestCutoutY != null) {
            getViewComponent().getSizeRepository().updateCutoutY(fRequestCutoutY.floatValue());
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
        DynamicIslandContentView tempShow = dynamicIslandEventCoordinator2 != null ? dynamicIslandEventCoordinator2.getTempShow() : null;
        Configuration configuration = this.previousConfig;
        if (configuration == null) {
            kotlin.jvm.internal.o.w("previousConfig");
            configuration = null;
        }
        boolean z3 = configuration.isNightModeActive() == newConfig.isNightModeActive();
        if (tempShow != null && z3) {
            removeDynamicIslandData(tempShow.getCurrentIslandData(), true);
        }
        Configuration configuration2 = this.previousConfig;
        if (configuration2 == null) {
            kotlin.jvm.internal.o.w("previousConfig");
            configuration2 = null;
        }
        boolean z4 = MiBlurCompat.getBackgroundBlurOpened(configuration2) != MiBlurCompat.getBackgroundBlurOpened(newConfig);
        if (z4) {
            updateBlurContainer(MiBlurCompat.getBackgroundBlurOpened(newConfig));
        }
        Configuration configuration3 = this.previousConfig;
        if (configuration3 == null) {
            kotlin.jvm.internal.o.w("previousConfig");
            configuration3 = null;
        }
        boolean zCompareConfigurations = compareConfigurations(configuration3, newConfig);
        Configuration configuration4 = this.previousConfig;
        if (configuration4 == null) {
            kotlin.jvm.internal.o.w("previousConfig");
            configuration4 = null;
        }
        configuration4.updateFrom(newConfig);
        if (zCompareConfigurations) {
            getViewComponent().getSizeRepository().updateIslandMaxWidth(0.0f);
            DynamicIslandWindowViewController.DynamicIslandCallback dynamicIslandCallback = getWindowViewController().getDynamicIslandCallback();
            if (dynamicIslandCallback != null) {
                dynamicIslandCallback.onDynamicIslandConfigChange();
            }
            getWindowViewController().getWindowState().setConfigChange(Boolean.FALSE);
            return;
        }
        for (DynamicIslandContentView dynamicIslandContentView : this.contentViewList) {
            if (dynamicIslandContentView.getCurrentIslandData() != null && ((currentIslandData = dynamicIslandContentView.getCurrentIslandData()) == null || (properties = currentIslandData.getProperties()) == null || properties.intValue() != 0 || z3)) {
                dynamicIslandContentView.calculateBigIslandWidth();
                dynamicIslandContentView.updateView(dynamicIslandContentView.getCurrentIslandData(), true, false);
            }
            updateExpandedView$default(this, dynamicIslandContentView, dynamicIslandContentView.getCurrentIslandData(), null, 4, null);
            if (z4) {
                DynamicIslandExpandedView expandedView = dynamicIslandContentView.getExpandedView();
                kotlin.jvm.internal.o.e(expandedView, "null cannot be cast to non-null type android.view.View");
                dynamicIslandContentView.updateBackgroundBg(expandedView);
            }
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator3 = this.eventCoordinator;
        if (dynamicIslandEventCoordinator3 != null) {
            DynamicIslandEventCoordinator.dispatchEvent$default(dynamicIslandEventCoordinator3, DynamicIslandEvent.ConfigChanged.INSTANCE, null, 2, null);
        }
        for (DynamicIslandContentFakeView dynamicIslandContentFakeView : this.fakeViewList) {
            if (dynamicIslandContentFakeView.getCurrentIslandData() != null) {
                dynamicIslandContentFakeView.updateView(dynamicIslandContentFakeView.getCurrentIslandData(), true, false);
            }
            updateExpandedView$default(this, dynamicIslandContentFakeView, dynamicIslandContentFakeView.getCurrentIslandData(), null, 4, null);
            if (z4) {
                FrameLayout fakeExpandedView = dynamicIslandContentFakeView.getFakeExpandedView();
                kotlin.jvm.internal.o.e(fakeExpandedView, "null cannot be cast to non-null type android.view.View");
                dynamicIslandContentFakeView.updateBackgroundBg(fakeExpandedView);
            }
        }
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        View viewFindViewById = findViewById(R.id.glow_effect_bottom_container);
        kotlin.jvm.internal.o.f(viewFindViewById, "findViewById(...)");
        this.mGlowEffectBottomContainer = (ViewGroup) viewFindViewById;
        View viewFindViewById2 = findViewById(R.id.glow_effect_top_container);
        kotlin.jvm.internal.o.f(viewFindViewById2, "findViewById(...)");
        this.mGlowEffectTopContainer = (ViewGroup) viewFindViewById2;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev == null) {
            return super.onInterceptTouchEvent(ev);
        }
        TouchEvent touchEvent = new TouchEvent(ev, TouchEvent.SOURCE_DYNAMIC_ISLAND);
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new C09021(touchEvent, null), 3, null);
        Boolean result = touchEvent.getResult();
        return result != null ? result.booleanValue() : super.onInterceptTouchEvent(ev);
    }

    public final void onIslandTempHide(boolean hide, DynamicIslandWindowState.TempHiddenType type) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            DynamicIslandEvent.IslandTempHiddenChanged islandTempHiddenChanged = DynamicIslandEvent.IslandTempHiddenChanged.INSTANCE;
            islandTempHiddenChanged.setHide(hide);
            islandTempHiddenChanged.setType(type);
            DynamicIslandEventCoordinator.dispatchEvent$default(dynamicIslandEventCoordinator, islandTempHiddenChanged, null, 2, null);
        }
    }

    public final void onKeyguardShowing(boolean isKeyguardShowing) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.onKeyguardShowing(isKeyguardShowing);
        }
    }

    public final void onLongPress(final DynamicIslandBaseContentView view, final DynamicIslandContentView state, final float rawY) {
        kotlin.jvm.internal.o.g(view, "view");
        Runnable runnable = new Runnable() { // from class: miui.systemui.dynamicisland.window.n
            @Override // java.lang.Runnable
            public final void run() {
                DynamicIslandWindowView.onLongPress$lambda$32(view, state, rawY);
            }
        };
        view.postDelayed(runnable, 20L);
        this.longPress = runnable;
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return super.onTouchEvent(event);
        }
        TouchEvent touchEvent = new TouchEvent(event, TouchEvent.SOURCE_DYNAMIC_ISLAND);
        AbstractC1178g.b(LifecycleOwnerKt.getLifecycleScope(this), null, null, new C09031(touchEvent, null), 3, null);
        Boolean result = touchEvent.getResult();
        return result != null ? result.booleanValue() : super.onTouchEvent(event);
    }

    @Override // com.android.systemui.settings.UserTracker.Callback
    public void onUserChanged(int newUser, Context userContext) throws PendingIntent.CanceledException {
        kotlin.jvm.internal.o.g(userContext, "userContext");
        Log.e(TAG, "onUserChanged: " + this.currentUserId + "   " + newUser);
        Iterator<T> it = this.dynamicIslandDataList.iterator();
        while (it.hasNext()) {
            removeDynamicIslandData$default(this, (DynamicIslandData) it.next(), false, 2, null);
        }
        this.currentUserId = newUser;
        Iterator<DynamicIslandData> it2 = this.dynamicIslandDataList.iterator();
        while (it2.hasNext()) {
            Integer properties = it2.next().getProperties();
            if (properties != null && properties.intValue() == 0) {
                it2.remove();
            }
        }
        Iterator<T> it3 = this.dynamicIslandDataList.iterator();
        while (it3.hasNext()) {
            addDynamicIslandData((DynamicIslandData) it3.next(), false, getWindowViewController().getIslandMaxWidth(), getWindowViewController().getCutoutY(), false);
        }
        onDeviceNotificationChanged(hasDeviceNotification());
    }

    public final void onWindowAnimExtendLifetimeEnd(DynamicIslandContentView state) {
        DynamicIslandData currentIslandData;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.onWindowAnimExtendLifetimeEnd((state == null || (currentIslandData = state.getCurrentIslandData()) == null) ? null : currentIslandData.getExtras());
        }
    }

    public final void onWindowAnimExtendLifetimeStart(DynamicIslandContentView state) {
        DynamicIslandData currentIslandData;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.onWindowAnimExtendLifetimeStart((state == null || (currentIslandData = state.getCurrentIslandData()) == null) ? null : currentIslandData.getExtras());
        }
    }

    public final void removeDynamicIslandData(DynamicIslandData dynamicIslandData, boolean noAnimation) {
        Bundle extras;
        if (assertUserSpace(dynamicIslandData)) {
            removeDynamicIslandData(dynamicIslandData != null ? dynamicIslandData.getKey() : null, (dynamicIslandData == null || (extras = dynamicIslandData.getExtras()) == null) ? null : extras.getString("miui.pkg.name"), false, noAnimation);
        }
    }

    public final List<DynamicIslandContentView> requestHasIsland(String pkg) {
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            return dynamicIslandEventCoordinator.hasIsland(pkg);
        }
        return null;
    }

    public final void setAnimRunning(boolean animRunning, boolean isFreeform) throws Resources.NotFoundException {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
        if (dynamicIslandEventCoordinator2 != null) {
            dynamicIslandEventCoordinator2.updateWindowHeightInAnimState(animRunning, isFreeform);
        }
        if (animRunning || (dynamicIslandEventCoordinator = this.eventCoordinator) == null) {
            return;
        }
        dynamicIslandEventCoordinator.onAnimationFinished();
    }

    public final void setClosingToExpanded(DynamicIslandContentView state, boolean isFreeform, boolean closingToExpanded) {
        DynamicIslandContentFakeView fakeView;
        if (state == null || (fakeView = state.getFakeView()) == null) {
            return;
        }
        fakeView.setClosingToExpanded(isFreeform, closingToExpanded);
    }

    public final void setContentViewList(CopyOnWriteArrayList<DynamicIslandContentView> copyOnWriteArrayList) {
        kotlin.jvm.internal.o.g(copyOnWriteArrayList, "<set-?>");
        this.contentViewList = copyOnWriteArrayList;
    }

    public final void setEffectSize(Rect rect) {
        kotlin.jvm.internal.o.g(rect, "rect");
    }

    public final void setEventCoordinator(DynamicIslandEventCoordinator dynamicIslandEventCoordinator) {
        this.eventCoordinator = dynamicIslandEventCoordinator;
    }

    public final void setHeadsUpHeight(int i3) throws PendingIntent.CanceledException {
        this._headsUpHeight = i3;
        if (i3 == 0 && this.touchOutsideInHeadsUp) {
            collapse("heads up");
        }
    }

    public final void setLight(boolean z3) {
        this.isLight = z3;
    }

    public final void setViewComponent(DynamicIslandViewComponent value) {
        kotlin.jvm.internal.o.g(value, "value");
        if (this._viewComponent != null) {
            throw new IllegalStateException("DynamicIslandViewComponent was already initialized.");
        }
        this._viewComponent = value;
        initEventCoordinator();
    }

    public final boolean touchInHeadsUpZone(int y3) {
        I headsUpZone;
        L0.m mVar;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        return (dynamicIslandEventCoordinator == null || (headsUpZone = dynamicIslandEventCoordinator.getHeadsUpZone()) == null || (mVar = (L0.m) headsUpZone.getValue()) == null || ((Number) mVar.d()).intValue() == 0 || ((Number) mVar.e()).intValue() == 0 || y3 <= ((Number) mVar.d()).intValue() || y3 >= ((Number) mVar.e()).intValue()) ? false : true;
    }

    public final void updateAppCloseRect(Rect rect, DynamicIslandContentView realView) {
        DynamicIslandData currentIslandData;
        DynamicIslandData currentIslandData2;
        Bundle extras;
        Log.e(DynamicIslandConstants.TAG_DEBUG_ANIM, "updateAppCloseRect : " + rect);
        if (realView != null && (currentIslandData2 = realView.getCurrentIslandData()) != null && (extras = currentIslandData2.getExtras()) != null) {
            extras.putParcelable("position", rect);
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.positionChanged((realView == null || (currentIslandData = realView.getCurrentIslandData()) == null) ? null : currentIslandData.getExtras());
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    public final void updateDarkLightMode(boolean isLight) {
        this.isLight = isLight;
        for (DynamicIslandContentView dynamicIslandContentView : this.contentViewList) {
        }
    }

    public final void updateDynamicIslandView(final DynamicIslandData dynamicIslandData, final boolean expanded, final float maxWidth) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator;
        DynamicIslandContentFakeView fakeView;
        kotlin.jvm.internal.o.g(dynamicIslandData, "dynamicIslandData");
        Log.e(TAG, "updateDynamicIslandView");
        if (dynamicIslandData.getKey() == null) {
            return;
        }
        updateDynamicIslandDataList(dynamicIslandData);
        if (assertUserSpace(dynamicIslandData)) {
            Bundle extras = dynamicIslandData.getExtras();
            String string = extras != null ? extras.getString("miui.pkg.name") : null;
            final DynamicIslandContentView viewFromList = getViewFromList(dynamicIslandData.getKey());
            if ((viewFromList != null ? viewFromList.getState() : null) == null) {
                if (viewFromList != null) {
                    removeDynamicIslandData(viewFromList.getCurrentIslandData(), false);
                }
                addDynamicIslandData$default(this, dynamicIslandData, expanded, getWindowViewController().getIslandMaxWidth(), getWindowViewController().getCutoutY(), false, 16, null);
                return;
            }
            Bundle extras2 = dynamicIslandData.getExtras();
            notifyAddIsland(string, extras2 != null ? Integer.valueOf(extras2.getInt("miui.user.id")) : null, dynamicIslandData.getKey(), dynamicIslandData.getProperties());
            if (viewFromList != null) {
                viewFromList.updateView(dynamicIslandData, true, canUpdate(viewFromList));
            }
            if (viewFromList != null && (fakeView = viewFromList.getFakeView()) != null) {
                DynamicIslandBaseContentView.updateView$default(fakeView, dynamicIslandData, true, false, 4, null);
            }
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator2 = this.eventCoordinator;
            if ((dynamicIslandEventCoordinator2 == null || !dynamicIslandEventCoordinator2.get_userExpanded()) && canExpanded(expanded, dynamicIslandData.getView()) && (dynamicIslandEventCoordinator = this.eventCoordinator) != null) {
                dynamicIslandEventCoordinator.onHeightChangedFirst();
            }
            OneShotPreDrawListener.add(this, new Runnable() { // from class: miui.systemui.dynamicisland.window.DynamicIslandWindowView$updateDynamicIslandView$$inlined$doOnPreDraw$1
                @Override // java.lang.Runnable
                public final void run() throws Resources.NotFoundException, PendingIntent.CanceledException {
                    DynamicIslandState state;
                    DynamicIslandExpandedView expandedView;
                    DynamicIslandExpandedView expandedView2;
                    View view = dynamicIslandData.getView();
                    Integer numValueOf = view != null ? Integer.valueOf(view.getWidth()) : null;
                    View view2 = dynamicIslandData.getView();
                    Integer numValueOf2 = view2 != null ? Integer.valueOf(view2.getHeight()) : null;
                    DynamicIslandContentView dynamicIslandContentView = viewFromList;
                    Integer numValueOf3 = (dynamicIslandContentView == null || (expandedView2 = dynamicIslandContentView.getExpandedView()) == null) ? null : Integer.valueOf(expandedView2.getWidth());
                    DynamicIslandContentView dynamicIslandContentView2 = viewFromList;
                    Log.e("DynamicIslandWindowViewImpl", "expanded" + numValueOf + ", " + numValueOf2 + ", " + numValueOf3 + ", " + ((dynamicIslandContentView2 == null || (expandedView = dynamicIslandContentView2.getExpandedView()) == null) ? null : Integer.valueOf(expandedView.getMeasuredHeight())));
                    boolean zContains = this.getContentViewList().contains(viewFromList);
                    StringBuilder sb = new StringBuilder();
                    sb.append("updateDynamicIslandView contentViewList ");
                    sb.append(zContains);
                    Log.e("DynamicIslandWindowViewImpl", sb.toString());
                    if (this.getContentViewList().contains(viewFromList)) {
                        DynamicIslandContentView viewFromList2 = this.getViewFromList(dynamicIslandData.getKey());
                        if (viewFromList2 == null) {
                            DynamicIslandWindowView dynamicIslandWindowView = this;
                            DynamicIslandWindowView.addDynamicIslandData$default(dynamicIslandWindowView, dynamicIslandData, expanded, maxWidth, dynamicIslandWindowView.getWindowViewController().getCutoutY(), false, 16, null);
                            return;
                        }
                        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewFromList2.getExpandedViewMaxWidth(), Integer.MIN_VALUE);
                        DynamicIslandUtils dynamicIslandUtils = DynamicIslandUtils.INSTANCE;
                        Context context = this.getContext();
                        kotlin.jvm.internal.o.f(context, "getContext(...)");
                        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(dynamicIslandUtils.getScreenHeightOld(context), Integer.MIN_VALUE);
                        DynamicIslandExpandedView expandedView3 = viewFromList2.getExpandedView();
                        if (expandedView3 != null) {
                            expandedView3.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                        }
                        if (dynamicIslandData.getView() != null) {
                            DynamicIslandWindowView dynamicIslandWindowView2 = this;
                            DynamicIslandData dynamicIslandData2 = dynamicIslandData;
                            DynamicIslandExpandedView expandedView4 = viewFromList2.getExpandedView();
                            dynamicIslandWindowView2.updateExpandedView(viewFromList2, dynamicIslandData2, expandedView4 != null ? Integer.valueOf(expandedView4.getMeasuredHeight()) : null);
                        }
                        DynamicIslandState state2 = viewFromList2.getState();
                        if (state2 != null) {
                            String key = dynamicIslandData.getKey();
                            kotlin.jvm.internal.o.d(key);
                            state2.setUpdateKey(key);
                        }
                        viewFromList2.setCurrentIslandData(dynamicIslandData);
                        IslandTemplate islandTemplate = this.getWindowViewController().getIslandTemplate(dynamicIslandData);
                        Boolean boolValueOf = islandTemplate != null ? Boolean.valueOf(islandTemplate.getIslandOrder()) : null;
                        if ((expanded || kotlin.jvm.internal.o.c(boolValueOf, Boolean.TRUE)) && (state = viewFromList2.getState()) != null) {
                            state.setTime(Long.valueOf(System.currentTimeMillis()));
                        }
                        DynamicIslandState state3 = viewFromList2.getState();
                        if (state3 != null) {
                            state3.setUpdateOrder(boolValueOf);
                        }
                        DynamicIslandState state4 = viewFromList2.getState();
                        if (state4 != null) {
                            state4.setExpanded(this.canExpanded(expanded, dynamicIslandData.getView()));
                        }
                        if (this.canUpdate(viewFromList2)) {
                            if (this.getWindowViewController().getIsAppAnimRunning() && (viewFromList2.getState() instanceof DynamicIslandState.BigIsland)) {
                                this.getWindowViewController().updateAppRect(viewFromList2, false);
                                DynamicIslandContentFakeView fakeView2 = viewFromList2.getFakeView();
                                if (fakeView2 != null) {
                                    fakeView2.setForceUpdateBigIslandView(true);
                                }
                            }
                            DynamicIslandEventCoordinator eventCoordinator = this.getEventCoordinator();
                            if (eventCoordinator != null) {
                                eventCoordinator.dispatchEvent(DynamicIslandEvent.UpdateDynamicIsland.INSTANCE, viewFromList2);
                            }
                            DynamicIslandContentFakeView fakeView3 = viewFromList2.getFakeView();
                            if (fakeView3 == null) {
                                return;
                            }
                            fakeView3.setForceUpdateBigIslandView(false);
                        }
                    }
                }
            });
            getWindowViewController().updateChronometersIn(viewFromList, viewFromList.getFakeView(), dynamicIslandData);
        }
    }

    public final void updateExpandedViewScaleForFreeform(DynamicIslandContentView view, Float progress, boolean reset) {
        DynamicIslandContentFakeView fakeView;
        if (progress != null) {
            float fFloatValue = progress.floatValue();
            if (view == null || (fakeView = view.getFakeView()) == null) {
                return;
            }
            fakeView.updateExpandedViewScaleForFreeform(fFloatValue, reset);
        }
    }

    public final void updateFreeformFakeView(DynamicIslandContentView state) {
        DynamicIslandData currentIslandData;
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            Bundle extras = null;
            DynamicIslandContentFakeView fakeView = state != null ? state.getFakeView() : null;
            if (state != null && (currentIslandData = state.getCurrentIslandData()) != null) {
                extras = currentIslandData.getExtras();
            }
            dynamicIslandEventCoordinator.updateFreeformFakeView(fakeView, state, extras);
        }
    }

    public final void updateHeadsUpZone(int height) throws PendingIntent.CanceledException {
        int iDpToPx;
        int i3;
        setHeadsUpHeight(height);
        DynamicIslandContentView currentExpandedState = getCurrentExpandedState();
        if (currentExpandedState == null || height == 0) {
            iDpToPx = 0;
            i3 = 0;
        } else {
            int expandedViewY = currentExpandedState.getExpandedViewY() + ((int) (currentExpandedState.getIslandViewHeight() * (currentExpandedState.getExpandedViewHeight() / currentExpandedState.getIslandViewHeight())));
            DynamicIslandUtils dynamicIslandUtils = DynamicIslandUtils.INSTANCE;
            Context context = getContext();
            kotlin.jvm.internal.o.f(context, "getContext(...)");
            iDpToPx = expandedViewY + dynamicIslandUtils.dpToPx(8, context);
            i3 = height + iDpToPx;
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.updateHeadsUpZone(new L0.m(Integer.valueOf(iDpToPx), Integer.valueOf(i3)));
        }
    }

    public final void updateIslandWindowAnimRunningState(boolean windowAnimRunning, DynamicIslandContentView view, boolean isFreeform) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.updateIslandWindowAnimRunning(windowAnimRunning, view, isFreeform);
        }
    }

    public final void updatePkgSupportFreeform(String pkg) {
        kotlin.jvm.internal.o.g(pkg, "pkg");
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            Context context = getContext();
            kotlin.jvm.internal.o.f(context, "getContext(...)");
            dynamicIslandEventCoordinator.updatePkgSupportFreeform(pkg, context);
        }
    }

    public final void updateStatusBarVisible(boolean visible) {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.updateStatusBarVisible(visible);
        }
    }

    public final void updateTouchRegion() throws Resources.NotFoundException {
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.updateTouchRegion();
        }
    }

    public final void updateViewStateWhenCloseEnd(DynamicIslandContentView state, boolean isFreeform) {
        AbstractC1178g.b(getWindowViewController().getUiScope(), null, null, new C09041(state, isFreeform, null), 3, null);
    }

    public final void updateViewStateWhenOpenAnimStart(DynamicIslandContentView state) {
        DynamicIslandContentFakeView fakeView;
        if (state != null && (fakeView = state.getFakeView()) != null) {
            fakeView.updateViewStateWhenOpenAnimStart();
        }
        DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
        if (dynamicIslandEventCoordinator != null) {
            dynamicIslandEventCoordinator.updateAppExpandedStateWhenAnimStart(state);
        }
    }

    public static /* synthetic */ void removeDynamicIslandData$default(DynamicIslandWindowView dynamicIslandWindowView, String str, String str2, boolean z3, boolean z4, int i3, Object obj) {
        if ((i3 & 4) != 0) {
            z3 = true;
        }
        if ((i3 & 8) != 0) {
            z4 = false;
        }
        dynamicIslandWindowView.removeDynamicIslandData(str, str2, z3, z4);
    }

    @Override // android.view.LifecycleOwner
    public LifecycleRegistry getLifecycle() {
        return (LifecycleRegistry) this.lifecycle.getValue();
    }

    public final void removeDynamicIslandData(String key, String pkg, boolean removeFromList, boolean noAnimation) {
        w wVar;
        Log.e(TAG, "removeDynamicIslandData: " + key);
        DynamicIslandContentView viewFromList = getViewFromList(key);
        if (viewFromList != null) {
            DynamicIslandState state = viewFromList.getState();
            if (state != null) {
                state.setDeleteNoAnimation(noAnimation);
            }
            DynamicIslandEventCoordinator dynamicIslandEventCoordinator = this.eventCoordinator;
            if (dynamicIslandEventCoordinator != null) {
                dynamicIslandEventCoordinator.dispatchEvent(DynamicIslandEvent.DeletedDynamicIsland.INSTANCE, viewFromList);
            }
            clearAfterDelete(viewFromList.getCurrentIslandData(), key, removeFromList);
            wVar = w.f1623a;
        } else {
            wVar = null;
        }
        if (wVar == null) {
            clearAfterDelete(null, key, removeFromList);
        }
    }
}