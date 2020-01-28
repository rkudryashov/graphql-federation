package io.graphqlfederation.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.micronaut.context.annotation.Property
import java.nio.charset.Charset
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Singleton

@Singleton
class JwtService(
    @Property(name = "auth.token.secret")
    private val secret: String,
    @Property(name = "auth.token.validityTime")
    private val tokenValidityTime: Long
) {

    fun generateToken(subject: String): String {
        val signatureAlgorithm = SignatureAlgorithm.HS256
        val now = LocalDateTime.now()

        val signingKey = getSigningKey()
        val builder = Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(now.plusMinutes(tokenValidityTime)))
            .signWith(signingKey, signatureAlgorithm)
        return builder.compact()
    }

    /**
     * Throws exception if it is not a signed JWT
     */
    fun verify(jws: String): Claims = Jwts.parser()
        .setSigningKey(getSigningKey())
        .parseClaimsJws(jws)
        .body

    private fun getSigningKey(): Key = Keys.hmacShaKeyFor(secret.toByteArray(Charset.forName("UTF-8")))

    private fun toDate(ldt: LocalDateTime) = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
}
