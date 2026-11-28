package tn.esprit.insursmart.gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Complement du CircuitBreaker : si la Gateway ne peut pas joindre contract-service
 * (aucune instance dans Eureka, 502/503/504), on renvoie la meme reponse de repli
 * que FallbackController au lieu de la page Whitelabel.
 */
@Component
public class ContractFallbackFilter extends OncePerRequestFilter {

    private static final List<String> CONTRACT_PATHS =
            List.of("/api/contracts", "/api/insurance-types", "/api/subscriptions");

    private static final String FALLBACK_JSON =
            "{\"message\":\"Service Contrat temporairement indisponible\",\"degraded\":true}";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return CONTRACT_PATHS.stream().noneMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(request, new HttpServletResponseWrapper(response) {
                @Override
                public void sendError(int sc) throws IOException {
                    if (!writeFallbackIfGatewayError(response, sc)) super.sendError(sc);
                }

                @Override
                public void sendError(int sc, String msg) throws IOException {
                    if (!writeFallbackIfGatewayError(response, sc)) super.sendError(sc, msg);
                }
            });
        } catch (Exception e) {            // ex. "503 Unable to find instance for CONTRACT-SERVICE"
            if (response.isCommitted()) throw e;
            writeFallback(response);
        }
    }

    private static boolean writeFallbackIfGatewayError(HttpServletResponse response, int status) throws IOException {
        if (status == 502 || status == 503 || status == 504) {
            writeFallback(response);
            return true;
        }
        return false;
    }

    private static void writeFallback(HttpServletResponse response) throws IOException {
        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);     // meme statut que FallbackController (atelier)
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(FALLBACK_JSON);
    }
}