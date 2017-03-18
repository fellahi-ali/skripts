import io.kotlintest.Duration
import io.kotlintest.specs.StringSpec
import jdk.nashorn.internal.ir.ObjectNode
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

class MockRetrofitTest : StringSpec() { init {

    val retrofit = Retrofit.Builder()
            .baseUrl("http://httpbin.org")
            .build()

    val mockRetrofit = MockRetrofit.Builder(retrofit)
            .networkBehavior(
                    NetworkBehavior.create().apply {
                        setDelay(300, TimeUnit.MILLISECONDS)
                        setErrorPercent(30)
                        setVariancePercent(60)
                        setFailurePercent(10)
                    }
            ).build()

    val httpbin = mockRetrofit
            .create(PingService::class.java)


    repeat(10) { i ->
        "Ping ${i}" {
            val service = httpbin.returningResponse(true)
            service.ping().execute().isSuccessful shouldBe true
        }.config(timeout = Duration(400, TimeUnit.MILLISECONDS))
    }


}}


interface PingService {

    /**   Returns Origin IP   */
    @GET("/ping")
    fun ping(): Call<Response<Boolean>>

}