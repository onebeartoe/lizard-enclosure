
	<div class="copyspace">
	    <h3>Lizard Enclosure - Ultraviolet Lamps</h3>
        
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
                    <a href="${pageContext.request.contextPath}/controls?uvLight=${uvLightAction}">
                        <img src="${pageContext.request.contextPath}/images/${imagePath}">
                        <br/>
                        <b>${uvLightState}</b>
                    </a>
                </center>
            
	    </div>                        
	</div>

	<div class="copyspace">
	    <h3>Lizard Enclosure - Humidifier</h3>
          
            <div class="featuredProject">
                <center>
                    <a href="${pageContext.request.contextPath}/controls/humidifier?power=${humidifierNextState}">
                        <img src="${pageContext.request.contextPath}/images/${humidifierImagePath}">
                        <br/>
                        <b>${humidifierCurrentState}</b>
                        <br/>
                        enclosure: ${applicationScope.enclosure}
                    </a>
                </center>
	    </div>                        
	</div>

	<div class="copyspace">
	    <h3>Lizard Enclosure - Camera</h3>
          
            <div class="featuredProject">
                <br/>
                
                <center>                    
                    <input type="checkbox" id="cameraCheckbox">
                    <label for="cameraCheckbox">enabled</label>
                    
                    <br/>
                    <br/>

                    <button>Take Snapshot</button>
                </center>
	    </div>                        
	</div>
