package es.codeurjc.books.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static es.codeurjc.books.security.Constants.*;


public class TokenProvider {

	private TokenProvider() {}

	public static String generateToken(Authentication authentication) {
		final String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		return Jwts.builder()
				.setIssuer(ISSUER_TOKEN)
				.claim(AUTHORITIES_KEY, authorities)
				.setSubject(authentication.getName())
				.signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
				.compact();
	}

	public static UsernamePasswordAuthenticationToken getAuthentication(final String token,
			final UserDetails userDetails) {

		final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
		final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
		final Claims claims = claimsJws.getBody();

		final Collection<SimpleGrantedAuthority> authorities =
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());

		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	}

	public static String getNick(final String token) {
		final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
		final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
		return claimsJws.getBody().getSubject();
	}

}