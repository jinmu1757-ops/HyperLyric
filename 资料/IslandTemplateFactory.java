package miui.systemui.dynamicisland.template;

import L0.c;
import M0.d;
import M0.f;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData;
import f1.E;
import f1.F;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.jvm.internal.h;
import kotlin.jvm.internal.o;
import miui.systemui.controlcenter.panel.secondary.SecondaryParamsKt;
import miui.systemui.coroutines.Dispatchers;
import miui.systemui.dynamicisland.DynamicIslandConstants;
import miui.systemui.dynamicisland.IslandParamsException;
import miui.systemui.dynamicisland.dagger.DynamicIslandScope;
import miui.systemui.dynamicisland.dagger.qualifiers.DynamicIsland;
import miui.systemui.dynamicisland.model.BigIslandArea;
import miui.systemui.dynamicisland.model.ImageTextInfo;
import miui.systemui.dynamicisland.model.IslandTemplate;
import miui.systemui.dynamicisland.model.SmallIslandArea;
import miui.systemui.dynamicisland.template.IslandTemplateBuilder;
import miui.systemui.util.VolumeUtils;
import miuix.mipalette.MiPalette;

/* JADX INFO: loaded from: classes3.dex */
@DynamicIslandScope
public final class IslandTemplateFactory {
    private static final String AREA_LEFT = "area_left";
    private static final String AREA_RIGHT = "area_right";
    private static final String AREA_SMALL = "area_small";
    public static final Companion Companion = new Companion(null);
    private static final String TAG = "IslandTemplateFactory";
    private final Map<String, IslandTemplateBuilder> bigIslandBuilderMap;
    private final Map<String, IslandTemplateBuilder> fakeBigIslandBuilderMap;
    private final Map<String, IslandTemplateBuilder> fakeSmallIslandBuilderMap;
    private final IslandTemplateBuilder.Factory islandTemplateBuilderFactory;
    private final Map<String, IslandTemplateBuilder> smallIslandBuilderMap;
    private final Map<String, IslandTemplate> templateMap;
    private final E uiScope;

    public static final class Companion {
        public /* synthetic */ Companion(h hVar) {
            this();
        }

        private Companion() {
        }
    }

    /* JADX INFO: renamed from: miui.systemui.dynamicisland.template.IslandTemplateFactory$createBigIslandTemplate$1, reason: invalid class name */
    @f(c = "miui.systemui.dynamicisland.template.IslandTemplateFactory", f = "IslandTemplateFactory.kt", l = {344}, m = "createBigIslandTemplate")
    public static final class AnonymousClass1 extends d {
        int label;
        /* synthetic */ Object result;

        public AnonymousClass1(K0.d dVar) {
            super(dVar);
        }

        @Override // M0.a
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return IslandTemplateFactory.this.createBigIslandTemplate(null, null, null, null, false, null, this);
        }
    }

    /* JADX INFO: renamed from: miui.systemui.dynamicisland.template.IslandTemplateFactory$createBigIslandTemplateView$1, reason: invalid class name and case insensitive filesystem */
    @f(c = "miui.systemui.dynamicisland.template.IslandTemplateFactory", f = "IslandTemplateFactory.kt", l = {SecondaryParamsKt.FROM_BT, 154, 155, 178, 190, 191}, m = "createBigIslandTemplateView")
    public static final class C06591 extends d {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        int label;
        /* synthetic */ Object result;

        public C06591(K0.d dVar) {
            super(dVar);
        }

        @Override // M0.a
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return IslandTemplateFactory.this.createBigIslandTemplateView(null, null, null, null, false, null, this);
        }
    }

    /* JADX INFO: renamed from: miui.systemui.dynamicisland.template.IslandTemplateFactory$createSmallIslandTemplateView$1, reason: invalid class name and case insensitive filesystem */
    @f(c = "miui.systemui.dynamicisland.template.IslandTemplateFactory", f = "IslandTemplateFactory.kt", l = {VolumeUtils.MIPLAY_DEVICE_TYPE_WIRED_HEADSET, 224, 236, 238}, m = "createSmallIslandTemplateView")
    public static final class C06601 extends d {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        /* synthetic */ Object result;

        public C06601(K0.d dVar) {
            super(dVar);
        }

        @Override // M0.a
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return IslandTemplateFactory.this.createSmallIslandTemplateView(null, null, null, null, false, null, this);
        }
    }

    public IslandTemplateFactory(@DynamicIsland E scope, IslandTemplateBuilder.Factory islandTemplateBuilderFactory) {
        o.g(scope, "scope");
        o.g(islandTemplateBuilderFactory, "islandTemplateBuilderFactory");
        this.islandTemplateBuilderFactory = islandTemplateBuilderFactory;
        this.uiScope = F.g(scope, Dispatchers.INSTANCE.getMain());
        this.bigIslandBuilderMap = new LinkedHashMap();
        this.smallIslandBuilderMap = new LinkedHashMap();
        this.templateMap = new LinkedHashMap();
        this.fakeBigIslandBuilderMap = new LinkedHashMap();
        this.fakeSmallIslandBuilderMap = new LinkedHashMap();
        MiPalette.init();
    }

    private final String chooseModule(IslandTemplate islandTemplate, String str) throws IslandParamsException {
        Integer type;
        Integer type2;
        Integer type3;
        Integer type4;
        ImageTextInfo imageTextInfoRight;
        Integer type5;
        BigIslandArea bigIslandArea = islandTemplate.getBigIslandArea();
        SmallIslandArea smallIslandArea = islandTemplate.getSmallIslandArea();
        int iHashCode = str.hashCode();
        if (iHashCode == -100870023) {
            if (!str.equals(AREA_LEFT)) {
                return "";
            }
            ImageTextInfo imageTextInfoLeft = bigIslandArea != null ? bigIslandArea.getImageTextInfoLeft() : null;
            if ((imageTextInfoLeft != null ? imageTextInfoLeft.getType() : null) == null || (((type = imageTextInfoLeft.getType()) == null || type.intValue() != 1) && ((type2 = imageTextInfoLeft.getType()) == null || type2.intValue() != 5))) {
                throw new IslandParamsException("bigIslandArea.imageTextInfoLeft.type is null or not 1 or 5");
            }
            Integer type6 = imageTextInfoLeft.getType();
            if (type6 != null && type6.intValue() == 1) {
                return DynamicIslandConstants.MODULE_IMAGE_TEXT_1;
            }
            Integer type7 = imageTextInfoLeft.getType();
            return (type7 != null && type7.intValue() == 5) ? DynamicIslandConstants.MODULE_ICON_FIXED_WIDTH_TEXT : "";
        }
        if (iHashCode != 1173657578) {
            if (iHashCode == 1174694613 && str.equals(AREA_SMALL)) {
                return (smallIslandArea != null ? smallIslandArea.getCombinePicInfo() : null) != null ? DynamicIslandConstants.MODULE_COMBINE_PIC : (bigIslandArea == null || (imageTextInfoRight = bigIslandArea.getImageTextInfoRight()) == null || (type5 = imageTextInfoRight.getType()) == null || type5.intValue() != 6) ? DynamicIslandConstants.MODULE_PIC_SMALL_ISLAND : DynamicIslandConstants.MODULE_SMALL_TEXT_OVER_ICON;
            }
            return "";
        }
        if (!str.equals(AREA_RIGHT)) {
            return "";
        }
        ImageTextInfo imageTextInfoRight2 = bigIslandArea != null ? bigIslandArea.getImageTextInfoRight() : null;
        if (imageTextInfoRight2 != null && (((type3 = imageTextInfoRight2.getType()) == null || type3.intValue() != 1) && ((type4 = imageTextInfoRight2.getType()) == null || type4.intValue() != 5))) {
            ImageTextInfo imageTextInfoRight3 = bigIslandArea.getImageTextInfoRight();
            Integer type8 = imageTextInfoRight3 != null ? imageTextInfoRight3.getType() : null;
            if (type8 != null && type8.intValue() == 2) {
                return DynamicIslandConstants.MODULE_IMAGE_TEXT_2;
            }
            if (type8 != null && type8.intValue() == 3) {
                return DynamicIslandConstants.MODULE_IMAGE_TEXT_3;
            }
            if (type8 != null && type8.intValue() == 4) {
                return DynamicIslandConstants.MODULE_IMAGE_TEXT_4;
            }
            if (type8 != null && type8.intValue() == 6) {
                return DynamicIslandConstants.MODULE_TEXT_OVER_ICON;
            }
        }
        if ((bigIslandArea != null ? bigIslandArea.getFixedWidthDigitInfo() : null) != null) {
            return DynamicIslandConstants.MODULE_FIXED_WIDTH_DIGIT;
        }
        if ((bigIslandArea != null ? bigIslandArea.getSameWidthDigitInfo() : null) != null) {
            return DynamicIslandConstants.MODULE_SAME_WIDTH_DIGIT;
        }
        if ((bigIslandArea != null ? bigIslandArea.getProgressTextInfo() : null) != null) {
            return DynamicIslandConstants.MODULE_PROGRESS_TEXT;
        }
        if ((bigIslandArea != null ? bigIslandArea.getTextInfo() : null) != null) {
            return DynamicIslandConstants.MODULE_TEXT;
        }
        return (bigIslandArea != null ? bigIslandArea.getPicInfo() : null) != null ? DynamicIslandConstants.MODULE_PIC : "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:131:0x02c3  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x02c5  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x02d5  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x02fd A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:139:0x02fe  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0327 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0328  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01c5  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x01c7  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01d7  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x01ff A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0200  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0229 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:8:0x001c  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x022a  */
    @android.annotation.SuppressLint({"SuspiciousIndentation"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object createBigIslandTemplateView(android.content.Context r18, miui.systemui.dynamicisland.model.IslandTemplate r19, com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData r20, android.view.ViewGroup r21, boolean r22, U0.o r23, K0.d r24) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 848
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.systemui.dynamicisland.template.IslandTemplateFactory.createBigIslandTemplateView(android.content.Context, miui.systemui.dynamicisland.model.IslandTemplate, com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData, android.view.ViewGroup, boolean, U0.o, K0.d):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0157 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0158  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x01de A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x001c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object createSmallIslandTemplateView(android.content.Context r17, miui.systemui.dynamicisland.model.IslandTemplate r18, com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData r19, android.view.ViewGroup r20, boolean r21, U0.o r22, K0.d r23) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 503
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.systemui.dynamicisland.template.IslandTemplateFactory.createSmallIslandTemplateView(android.content.Context, miui.systemui.dynamicisland.model.IslandTemplate, com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData, android.view.ViewGroup, boolean, U0.o, K0.d):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object createBigIslandTemplate(android.content.Context r12, com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData r13, miui.systemui.dynamicisland.model.IslandTemplate r14, android.view.ViewGroup r15, boolean r16, U0.o r17, K0.d r18) {
        /*
            r11 = this;
            r0 = r18
            boolean r1 = r0 instanceof miui.systemui.dynamicisland.template.IslandTemplateFactory.AnonymousClass1
            if (r1 == 0) goto L17
            r1 = r0
            miui.systemui.dynamicisland.template.IslandTemplateFactory$createBigIslandTemplate$1 r1 = (miui.systemui.dynamicisland.template.IslandTemplateFactory.AnonymousClass1) r1
            int r2 = r1.label
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            r4 = r2 & r3
            if (r4 == 0) goto L17
            int r2 = r2 - r3
            r1.label = r2
            r2 = r11
        L15:
            r9 = r1
            goto L1e
        L17:
            miui.systemui.dynamicisland.template.IslandTemplateFactory$createBigIslandTemplate$1 r1 = new miui.systemui.dynamicisland.template.IslandTemplateFactory$createBigIslandTemplate$1
            r2 = r11
            r1.<init>(r0)
            goto L15
        L1e:
            java.lang.Object r0 = r9.result
            java.lang.Object r1 = L0.c.c()
            int r3 = r9.label
            r10 = 0
            r4 = 1
            if (r3 == 0) goto L3a
            if (r3 != r4) goto L32
            G0.k.b(r0)     // Catch: java.lang.Exception -> L30
            goto L73
        L30:
            r0 = move-exception
            goto L77
        L32:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L3a:
            G0.k.b(r0)
            java.lang.String r0 = "IslandTemplateFactory"
            if (r13 == 0) goto L46
            java.lang.String r3 = r13.getTickerData()     // Catch: java.lang.Exception -> L30
            goto L47
        L46:
            r3 = r10
        L47:
            java.lang.String r3 = miui.systemui.util.CommonUtils.encodeDataToBase64(r3)     // Catch: java.lang.Exception -> L30
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L30
            r5.<init>()     // Catch: java.lang.Exception -> L30
            java.lang.String r6 = "createBigIslandTemplate: "
            r5.append(r6)     // Catch: java.lang.Exception -> L30
            r5.append(r3)     // Catch: java.lang.Exception -> L30
            java.lang.String r3 = r5.toString()     // Catch: java.lang.Exception -> L30
            android.util.Log.d(r0, r3)     // Catch: java.lang.Exception -> L30
            if (r14 == 0) goto L76
            r9.label = r4     // Catch: java.lang.Exception -> L30
            r2 = r11
            r3 = r12
            r4 = r14
            r5 = r13
            r6 = r15
            r7 = r16
            r8 = r17
            java.lang.Object r0 = r2.createBigIslandTemplateView(r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Exception -> L30
            if (r0 != r1) goto L73
            return r1
        L73:
            android.view.View r0 = (android.view.View) r0     // Catch: java.lang.Exception -> L30
            r10 = r0
        L76:
            return r10
        L77:
            r0.printStackTrace()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.systemui.dynamicisland.template.IslandTemplateFactory.createBigIslandTemplate(android.content.Context, com.android.systemui.plugins.miui.dynamicisland.DynamicIslandData, miui.systemui.dynamicisland.model.IslandTemplate, android.view.ViewGroup, boolean, U0.o, K0.d):java.lang.Object");
    }

    public final Object createSmallIslandTemplate(Context context, DynamicIslandData dynamicIslandData, IslandTemplate islandTemplate, ViewGroup viewGroup, boolean z2, U0.o oVar, K0.d dVar) {
        if (islandTemplate == null) {
            return null;
        }
        Object objCreateSmallIslandTemplateView = createSmallIslandTemplateView(context, islandTemplate, dynamicIslandData, viewGroup, z2, oVar, dVar);
        return objCreateSmallIslandTemplateView == c.c() ? objCreateSmallIslandTemplateView : (View) objCreateSmallIslandTemplateView;
    }

    public final void hideView(DynamicIslandData dynamicIslandData, boolean z2) {
        Integer properties;
        Log.d(TAG, "hideView " + (dynamicIslandData != null ? dynamicIslandData.getKey() : null) + ", prop " + (dynamicIslandData != null ? dynamicIslandData.getProperties() : null));
        if (dynamicIslandData == null || (properties = dynamicIslandData.getProperties()) == null || properties.intValue() != 0) {
            if (z2) {
                if (this.fakeBigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                    IslandTemplateBuilder islandTemplateBuilder = this.fakeBigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                    if (islandTemplateBuilder != null) {
                        islandTemplateBuilder.hideModuleView();
                    }
                }
                if (this.fakeSmallIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                    IslandTemplateBuilder islandTemplateBuilder2 = this.fakeSmallIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                    if (islandTemplateBuilder2 != null) {
                        islandTemplateBuilder2.hideModuleView();
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.bigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder3 = this.bigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder3 != null) {
                    islandTemplateBuilder3.hideModuleView();
                }
            }
            if (this.smallIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder4 = this.smallIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder4 != null) {
                    islandTemplateBuilder4.hideModuleView();
                }
            }
        }
    }

    public final void removeTemplate(String key) {
        IslandTemplateBuilder islandTemplateBuilderRemoveModuleView;
        IslandTemplateBuilder islandTemplateBuilderRemoveModuleView2;
        IslandTemplateBuilder islandTemplateBuilderRemoveModuleView3;
        IslandTemplateBuilder islandTemplateBuilderRemoveModuleView4;
        IslandTemplateBuilder islandTemplateBuilderRemoveModuleView5;
        IslandTemplateBuilder islandTemplateBuilderRemoveModuleView6;
        o.g(key, "key");
        if (this.bigIslandBuilderMap.containsKey(key)) {
            IslandTemplateBuilder islandTemplateBuilder = this.bigIslandBuilderMap.get(key);
            IslandTemplateBuilder islandTemplateBuilder2 = this.fakeBigIslandBuilderMap.get(key);
            IslandTemplate islandTemplate = this.templateMap.get(key);
            if (islandTemplate != null) {
                String strChooseModule = chooseModule(islandTemplate, AREA_LEFT);
                String strChooseModule2 = chooseModule(islandTemplate, AREA_RIGHT);
                if (islandTemplateBuilder != null && (islandTemplateBuilderRemoveModuleView5 = islandTemplateBuilder.removeModuleView(strChooseModule)) != null && (islandTemplateBuilderRemoveModuleView6 = islandTemplateBuilderRemoveModuleView5.removeModuleView(strChooseModule2)) != null) {
                    islandTemplateBuilderRemoveModuleView6.removeIslandView();
                }
                if (islandTemplateBuilder2 != null && (islandTemplateBuilderRemoveModuleView3 = islandTemplateBuilder2.removeModuleView(strChooseModule)) != null && (islandTemplateBuilderRemoveModuleView4 = islandTemplateBuilderRemoveModuleView3.removeModuleView(strChooseModule2)) != null) {
                    islandTemplateBuilderRemoveModuleView4.removeIslandView();
                }
            }
        }
        this.bigIslandBuilderMap.remove(key);
        this.fakeBigIslandBuilderMap.remove(key);
        if (this.smallIslandBuilderMap.containsKey(key)) {
            IslandTemplateBuilder islandTemplateBuilder3 = this.smallIslandBuilderMap.get(key);
            IslandTemplateBuilder islandTemplateBuilder4 = this.fakeSmallIslandBuilderMap.get(key);
            IslandTemplate islandTemplate2 = this.templateMap.get(key);
            if (islandTemplate2 != null) {
                String strChooseModule3 = chooseModule(islandTemplate2, AREA_SMALL);
                if (islandTemplateBuilder3 != null && (islandTemplateBuilderRemoveModuleView2 = islandTemplateBuilder3.removeModuleView(strChooseModule3)) != null) {
                    islandTemplateBuilderRemoveModuleView2.removeIslandView();
                }
                if (islandTemplateBuilder4 != null && (islandTemplateBuilderRemoveModuleView = islandTemplateBuilder4.removeModuleView(strChooseModule3)) != null) {
                    islandTemplateBuilderRemoveModuleView.removeIslandView();
                }
            }
        }
        this.smallIslandBuilderMap.remove(key);
        this.fakeSmallIslandBuilderMap.remove(key);
        this.templateMap.remove(key);
    }

    public final void showView(DynamicIslandData dynamicIslandData, boolean z2) {
        Integer properties;
        Log.d(TAG, "showView " + (dynamicIslandData != null ? dynamicIslandData.getKey() : null) + ", prop " + (dynamicIslandData != null ? dynamicIslandData.getProperties() : null));
        if (dynamicIslandData == null || (properties = dynamicIslandData.getProperties()) == null || properties.intValue() != 0) {
            if (z2) {
                if (this.fakeBigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                    IslandTemplateBuilder islandTemplateBuilder = this.fakeBigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                    if (islandTemplateBuilder != null) {
                        islandTemplateBuilder.showModuleView();
                    }
                }
                if (this.fakeSmallIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                    IslandTemplateBuilder islandTemplateBuilder2 = this.fakeSmallIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                    if (islandTemplateBuilder2 != null) {
                        islandTemplateBuilder2.showModuleView();
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.bigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder3 = this.bigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder3 != null) {
                    islandTemplateBuilder3.showModuleView();
                }
            }
            if (this.smallIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder4 = this.smallIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder4 != null) {
                    islandTemplateBuilder4.showModuleView();
                }
            }
        }
    }

    public final void updateCutoutWidth(DynamicIslandData dynamicIslandData, int i2, boolean z2) {
        Log.d(TAG, "updateCutoutWidth " + i2 + " " + z2);
        if (z2) {
            if (this.fakeBigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder = this.fakeBigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder != null) {
                    islandTemplateBuilder.updateCutoutWidth(i2);
                    return;
                }
                return;
            }
            return;
        }
        if (this.bigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
            IslandTemplateBuilder islandTemplateBuilder2 = this.bigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
            if (islandTemplateBuilder2 != null) {
                islandTemplateBuilder2.updateCutoutWidth(i2);
            }
        }
    }

    public final void updateLeftWidth(DynamicIslandData dynamicIslandData, int i2, boolean z2) {
        Log.d(TAG, "updateLeftWidth " + i2);
        if (z2) {
            if (this.fakeBigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder = this.fakeBigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder != null) {
                    islandTemplateBuilder.updateLeftWidth(i2);
                    return;
                }
                return;
            }
            return;
        }
        if (this.bigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
            IslandTemplateBuilder islandTemplateBuilder2 = this.bigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
            if (islandTemplateBuilder2 != null) {
                islandTemplateBuilder2.updateLeftWidth(i2);
            }
        }
    }

    public final void updateRightWidth(DynamicIslandData dynamicIslandData, int i2, boolean z2) {
        Log.d(TAG, "updateRightWidth " + i2);
        if (z2) {
            if (this.fakeBigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
                IslandTemplateBuilder islandTemplateBuilder = this.fakeBigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
                if (islandTemplateBuilder != null) {
                    islandTemplateBuilder.updateRightWidth(i2);
                    return;
                }
                return;
            }
            return;
        }
        if (this.bigIslandBuilderMap.containsKey(dynamicIslandData != null ? dynamicIslandData.getKey() : null)) {
            IslandTemplateBuilder islandTemplateBuilder2 = this.bigIslandBuilderMap.get(dynamicIslandData != null ? dynamicIslandData.getKey() : null);
            if (islandTemplateBuilder2 != null) {
                islandTemplateBuilder2.updateRightWidth(i2);
            }
        }
    }
}