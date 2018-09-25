package lean.java.example.fuse;

/**
 * Created by sunyong on 2018-09-25.
 * 熔断器接口
 */
public interface CircuitBreaker {

    /**
     * 重置熔断器
     */
    void reset();

    /**
     * 是否允许通过熔断器
     */
    boolean canPassCheck();

    /**
     * 统计失败次数
     */
    void countFailNum();
}
