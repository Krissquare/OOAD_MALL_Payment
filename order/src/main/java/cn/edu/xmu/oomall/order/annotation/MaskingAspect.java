package cn.edu.xmu.oomall.order.annotation;


import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.ResponseUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Pattern;

@Aspect
@Component
public class MaskingAspect {

    final private static Integer USER_LEVEL_0 = 0;

    final private static Integer USER_LEVEL_1 = 1;

    final private static Integer USER_LEVEL_2 = 2;

    final private static String PHONE_NUMBER_PATTERN = "^1[0-9]{10}$";

    @Pointcut("@annotation(cn.edu.xmu.oomall.order.annotation.Masking)")
    public void maskingAspect() {

    }

    @Before("maskingAspect()")
    public void before(JoinPoint joinPoint) {

    }

    @Around("maskingAspect()")
    public Object around(JoinPoint joinPoint) throws JsonProcessingException {
        Object[] args = joinPoint.getArgs();
        Object object = null;
        try {
            object = ((ProceedingJoinPoint) joinPoint).proceed(args);
        } catch (Throwable throwable) {

        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        String token = request.getHeader("authorization");
        if (token == null) {
            response.setStatus(401);
            return ResponseUtil.fail(ReturnNo.AUTH_NEED_LOGIN);
        }
        JwtHelper.UserAndDepart userAndDepart = (new JwtHelper()).verifyTokenAndGetClaims(token);
        Integer userLevel = null;
        if (null != userAndDepart) {
            userLevel = userAndDepart.getUserLevel();
        }
        if (userLevel !=null && userLevel.equals(USER_LEVEL_0)) {
            return masking(object, USER_LEVEL_0);
        } else if (userLevel !=null && userLevel.equals(USER_LEVEL_1)){
            return masking(object, USER_LEVEL_1);
        } else if (userLevel !=null && userLevel.equals(USER_LEVEL_2)){
            return masking(object, USER_LEVEL_2);
        } else {
            return null;
        }

    }

    private static Object masking(Object object, Integer level) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = JacksonUtil.toJson(object);
        JsonNode root = mapper.readTree(json);
        List<JsonNode> mobileNodes =  root.findParents("mobile");
        for (JsonNode mobileNode : mobileNodes) {
            String phoneNumber = mobileNode.get("mobile").asText();
            String maskedPhoneNumber = phoneNumberMasking(phoneNumber, level);
            ((ObjectNode) mobileNode).put("mobile", maskedPhoneNumber);
        }

        List<JsonNode> addressNodes =  root.findParents("address");
        for (JsonNode addressNode : addressNodes) {
            String address = addressNode.get("address").asText();
            String maskedAddress = addressMasking(address, level);
            ((ObjectNode) addressNode).put("address", maskedAddress);
        }

        return mapper.convertValue(root, object.getClass());
    }

    private static String phoneNumberMasking(String phoneNumber, Integer level) {
        boolean isMatch = Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
        if (isMatch) {
            if (level.equals(USER_LEVEL_0)) {
                return null;
            } else if (level.equals(USER_LEVEL_1)) {
                StringBuilder stringBuilder = new StringBuilder(phoneNumber);
                stringBuilder.replace(3, 7, "****");
                return stringBuilder.toString();
            } else if (level.equals(USER_LEVEL_2)) {
                return phoneNumber;
            }
        }
        return null;
    }

    private static String addressMasking(String address, Integer level) {
        if (level.equals(USER_LEVEL_0)) {
            return null;
        } else if (level.equals(USER_LEVEL_1)) {
            StringBuilder stringBuilder = new StringBuilder(address);
            int beginIndex = Math.min(3, stringBuilder.length() - 1);
            for (int i = beginIndex; i < stringBuilder.length(); i++) {
                stringBuilder.setCharAt(i, '*');
            }
            return stringBuilder.toString();
        } else if (level.equals(USER_LEVEL_2)) {
            return address;
        }
        return null;
    }

}
