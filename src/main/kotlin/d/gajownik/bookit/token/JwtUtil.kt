package d.gajownik.bookit.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.*


object JwtUtil {
    private const val SECRET_KEY =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9eyJzdWIiOiIxMjM0IiwiYXVkIjpbImFkbWluIl0sImlzcyI6Im1hc29uLm1ldGFtdWcubmV0IiwiZXhwIjox"
    private const val EXPIRATION_TIME: Long = 86400000 // 24 godziny

    fun generateToken(email: String, role: String, companyId: Long): String {
        val nowMillis = System.currentTimeMillis()
        val now = Date(nowMillis)
        val exp = Date(nowMillis + EXPIRATION_TIME)
        val bytes = Decoders.BASE64.decode(SECRET_KEY)
        val key = Keys.hmacShaKeyFor(bytes)
        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .claim("company_id", companyId)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    fun parseToken(token: String?): Jws<Claims> {
        val bytes = Decoders.BASE64.decode(SECRET_KEY)
        val key = Keys.hmacShaKeyFor(bytes)
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
    }
}