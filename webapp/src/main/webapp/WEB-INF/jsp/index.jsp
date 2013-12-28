
	<div class="copyspace">
            
	    <h3>Lizard Enclosure</h3>
        
            <c:choose>
                <c:when test="${uvLightState.equalsIgnoreCase('on')}">
                    <c:set var="imagePath" value="uv-light-on.png"/>
                    <c:set var="uvLightAction" value="off"/>
                </c:when>
                <c:otherwise>
                    <c:set var="imagePath" value="uv-light-off.png"/>
                    <c:set var="uvLightAction" value="on"/>
                </c:otherwise>
            </c:choose>            

            <div class="featuredProject">
                <center>
                    <a href="${pageContext.request.contextPath}/gateway?uvLight=${uvLightAction}">
                        <img src="images/${imagePath}">
                        <br/>
                        <b>${uvLightState}</b>
                    </a>
                </center>
            
	    </div>
            
	</div>
