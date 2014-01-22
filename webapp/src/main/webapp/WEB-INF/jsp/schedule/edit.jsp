
	<div class="copyspace">
            
	    <h3>Lizard Enclosure - Edit Cron Schedule</h3>           

            <div class="featuredProject">
                <center>
                    current cron schedule:
                    <br/>
                    <textarea rows="8" name="cronTable" cols="40">${cronTable}</textarea>
                    </b>

                    <form method="POST" action="${pageContext.request.contextPath}/schedule/confirm/edit">
                        <input type="submit" value="Confirm">
                    </form>
                </center>
            
	    </div>
            
	</div>
