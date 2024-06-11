# the concept of the flow from request to controller 
the description is on this app
FilterChain (in SecurityConfig Class) -> Intercept Token (in doInternalFilter method, JWTAuthenticationFilter Class ) -> Login Method  (in AuthController), then generate JWT Token
