package com.shark.ctrip.yapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.requests.Proxies;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.net.Proxy;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class YapiOperator {
    private final static String SAVE_URL = "http://10.5.119.87:9526/api/interface/up";
    private final static String LOGIN_URL = "http://10.5.119.87:9526/api/user/login";
    private final static String GET_URL = "http://10.5.119.87:9526/api/interface/get?id=";
    private Session session;

    private YapiOperator() {
    }

    public static YapiOperator build() throws CertificateException {
        YapiOperator o = new YapiOperator();
        o.session = Requests.session();
        Map<String, Object> body = new HashMap<>();
        body.put("email", "yh_yin@ctrip.com");
        body.put("password", "asdf1234");
        RawResponse resp = o.session.post(LOGIN_URL).body(body).send();
        if (resp.statusCode() != 200) {
            log.error("登录失败: {}", resp.readToText());
            throw new CertificateException("YAPI 认证错误。登录失败");
        }
        return o;
    }

    public static void main(String[] args) throws CertificateException, JsonProcessingException {
        YapiOperator o = YapiOperator.build();
        Definition d = new Definition();
        d.setResp("{\"type\":\"object\",\"title\":\"empty object\",\"properties\":{\"cardInfo\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"ctype\":{\"type\":\"number\",\"description\":\"证件类型\"},\"String\":{\"type\":\"string\",\"description\":\"cNumber\"},\"cDate\":{\"type\":\"string\",\"description\":\"到期时候\"}},\"required\":[\"ctype\",\"String\",\"cDate\"]},\"description\":\"证件列表\"},\"clkInfo\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"hkNumber\":{\"type\":\"string\",\"description\":\"航司二字码\"},\"clkCode\":{\"type\":\"string\",\"description\":\"常旅客卡号\"}},\"required\":[\"hkNumber\",\"clkCode\"]}},\"accountInfo\":{\"type\":\"object\",\"properties\":{\"account\":{\"type\":\"string\",\"description\":\"账户\"},\"telPhone\":{\"type\":\"string\",\"description\":\"手机号\"},\"vipLevel\":{\"type\":\"integer\"},\"areaCode\":{\"type\":\"string\",\"description\":\"手机区号\"},\"status\":{\"type\":\"string\",\"description\":\"身份状态 0拒绝，1正常，2待审核，3离职'\"}},\"required\":[\"account\",\"telPhone\",\"vipLevel\",\"areaCode\",\"status\"]},\"profile\":{\"type\":\"object\",\"properties\":{\"uid\":{\"type\":\"string\"},\"userName\":{\"type\":\"string\",\"description\":\"员工姓名\"},\"lastName\":{\"type\":\"string\",\"description\":\"英文姓名\"},\"firstName\":{\"type\":\"string\",\"description\":\"英文姓名\"},\"orgId\":{\"type\":\"string\",\"description\":\"组织id\"},\"telPhone\":{\"type\":\"string\",\"description\":\"手机号\"},\"areaCode\":{\"type\":\"string\",\"description\":\"手机区号\"},\"mailbox\":{\"type\":\"string\",\"description\":\"邮箱\"},\"gender\":{\"type\":\"string\",\"description\":\"性别\"},\"nationality\":{\"type\":\"string\",\"description\":\"国籍\"},\"birthday\":{\"type\":\"string\",\"description\":\"生日\"}},\"description\":\"个人资料\",\"required\":[\"uid\",\"userName\"]},\"configuration\":{\"type\":\"object\",\"properties\":{\"payConfigs\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"site\":{\"type\":\"string\",\"description\":\"产线 机票flight 火车票train 酒店hotel 用车car 国际机票flightintl\"},\"corpPayType\":{\"type\":\"string\",\"description\":\"支付类型 因公PUB 因私OWN\"},\"paytype\":{\"type\":\"array\",\"items\":{\"type\":\"string\"},\"description\":\"支付方式（ACCNT公司支付 PPAY个人支付）\"}},\"required\":[\"site\",\"paytype\",\"corpPayType\"]},\"description\":\"国内机票因公。统一 ACCNT; 个人支付 PPAY;\"},\"roleId\":{\"type\":\"integer\"},\"roleName\":{\"type\":\"string\"},\"travelStandard\":{\"type\":\"object\",\"properties\":{\"hotel\":{\"type\":\"object\",\"properties\":{},\"description\":\"酒店差标\"},\"flight\":{\"type\":\"object\",\"properties\":{\"discount\":{\"type\":\"string\",\"description\":\"折扣\"},\"clazz\":{\"type\":\"string\",\"description\":\"可订舱位\"},\"lowControl\":{\"type\":\"string\"},\"mileageControl\":{\"type\":\"string\"},\"advancedBook\":{\"type\":\"string\"},\"timeControl\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"description\":\"国内机票\",\"required\":[\"discount\",\"clazz\",\"lowControl\",\"mileageControl\",\"advancedBook\",\"timeControl\"]},\"intlFlight\":{\"type\":\"object\",\"properties\":{},\"description\":\"国际机票\"},\"car\":{\"type\":\"object\",\"properties\":{},\"description\":\"用车\"},\"train\":{\"type\":\"object\",\"properties\":{\"seatName\":{\"type\":\"string\"}},\"required\":[\"seatName\"],\"description\":\"火车\"}},\"description\":\"差标\"}},\"description\":\"员工配置。包括开关。差标等\",\"required\":[\"payConfigs\",\"roleId\",\"roleName\",\"travelStandard\"]}},\"required\":[\"cardInfo\",\"clkInfo\",\"accountInfo\",\"profile\",\"configuration\"]}");
        d.setReq("{\"type\":\"object\",\"title\":\"empty object\",\"properties\":{}}");
        System.out.println(o.saveYapi(17193, d));
        System.out.println(o.acquireDefinition(17193));
    }

    public String acquireYapiDefinition(Integer id) throws CertificateException {
        RawResponse resp = session.get(GET_URL + id).send();
        if (resp.statusCode() != 200) {
            log.error("设置获取失败 {}", resp.readToText());
            throw new IllegalArgumentException("未找到对应的YAPI设置。");
        }
        String def = resp.readToText();
        log.info("{}", def);
        return def;
    }

    public Definition acquireDefinition(int id) throws CertificateException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DEF def = om.readValue(acquireYapiDefinition(id), DEF.class);
        Definition d = new Definition();
        String queryPath = def.getData().getQuery_path().get("path").toString();
        d.setName(queryPath.substring(queryPath.lastIndexOf("/") + 1));
        d.setReq(def.getData().getReq_body_other());
        d.setResp(def.getData().getRes_body());
        return d;
    }

    public boolean saveYapi(int id, Definition definition) throws CertificateException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> value = om.readValue(acquireYapiDefinition(id), new TypeReference<HashMap<String, Object>>() {
        });
        value = (Map<String, Object>) value.get("data");
        value.remove("query_path");
        value.remove("edit_uid");
        value.remove("type");
        value.remove("index");
        value.remove("_id");
        value.remove("uid");
        value.remove("add_time");
        value.remove("up_time");
        value.remove("__v");
        value.remove("username");

        value.put("switch_notice", true);
        value.put("id", String.valueOf(id));
        value.put("req_body_other", definition.getReq());
        value.put("res_body", definition.getResp());
        String s = om.writeValueAsString(value);
        RawResponse resp = session.post(SAVE_URL)
                .proxy(Proxies.httpProxy("localhost", 55290))
                .headers(Collections.singletonMap("Content-Type", "application/json"))
                .body(s).send();
        if (resp.statusCode() != 200) {
            log.error("{} \n {}", s, resp.readToText());
            return false;
        }
        log.info("保存 {}\n{}", s, resp.readToText());
        return true;
    }

    @Data
    public static class DEF {
        private Integer errcode;
        private DATA data;
        private String errmsg;
    }

    @Data
    public static class DATA {
        private String req_body_other;
        private String res_body;
        private Map<String, ?> query_path;
    }

    @Data
    public static class Definition {
        private String queryPath;
        private String name;
        private String req;
        private String resp;
    }
}
