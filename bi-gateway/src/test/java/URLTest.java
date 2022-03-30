import cn.hutool.core.util.URLUtil;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/11/3
 */
public class URLTest {

    public static void main(String[] args) {

        String url = "http://bi-test.stnts.com:8089/datas/cooperation/info/list/full-name";
        System.out.println(URLUtil.getHost(URLUtil.toUrlForHttp(url)).getHost());
    }
}
