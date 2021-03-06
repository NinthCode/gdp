package com.paouke.gdp.baiduTranslate.service;

import com.paouke.gdp.baiduTranslate.bean.DictResultBean;
import com.paouke.gdp.baiduTranslate.bean.LangDetectBean;
import com.paouke.gdp.baiduTranslate.bean.WordsInfoBean;
import com.paouke.gdp.baiduTranslate.constant.GdpBaiduTranslateConstant;
import com.paouke.gdp.baiduTranslate.helper.CallBaiduInterfaceHelper;
import com.paouke.gdp.baiduTranslate.helper.TokenizerHelper;
import com.paouke.gdp.common.helper.HtmlResultHelper;
import com.paouke.gdp.common.utils.StringUtils;

/**
 * Created by nicot on 17-4-14.
 */
public class BaiduTranslateService {

    public String doTranslate(String words) {
        WordsInfoBean wordsInfoBean = new WordsInfoBean();
        wordsInfoBean.setWords(GdpBaiduTranslateConstant.CONFIG.get().isDevMode() ? StringUtils.splitWord(words) : words);
        TokenizerHelper.extractOper(wordsInfoBean);
        TokenizerHelper.extractAbst(wordsInfoBean);
        if(!wordsInfoBean.isForceLangType()) {
            LangDetectBean langDetectBean = CallBaiduInterfaceHelper.callLangDetectInterface(wordsInfoBean);
            if(langDetectBean.getError() == 0){
                wordsInfoBean.setSourceLangType(langDetectBean.getLangType());
                if(langDetectBean.getLangType().equals(GdpBaiduTranslateConstant.LangType.zh)) {
                    wordsInfoBean.setPurposeLangType(GdpBaiduTranslateConstant.LangType.en);
                } else {
                    wordsInfoBean.setPurposeLangType(GdpBaiduTranslateConstant.LangType.zh);
                }
            } else {
                return "";
            }
        }
        DictResultBean dictResultBean = CallBaiduInterfaceHelper.callV2transapiInterface(wordsInfoBean);
        if(dictResultBean.getErrorCode() != null) {
            return "翻译失败，error code：" + dictResultBean.getErrorCode();
        }
        return HtmlResultHelper.easyResultHtmlCreater(GdpBaiduTranslateConstant.ENGINE_NAME, dictResultBean.getSrc(), dictResultBean.getDst());
    }
}
