
	<div class="copyspace">
            
	    <h3>Lizard Enclosure - Edit Cron Schedule</h3>           

            <div class="featuredProject">
                <center>
                    <form method="POST" action="${pageContext.request.contextPath}/schedule/confirm/edit">
                        current cron schedule:

                        <br/>

                        <textarea rows="8" name="newSchedlue" cols="40">${cronTable}</textarea>

                        </b>
                   
                        <input type="submit" value="Confirm">
                    </form>
                </center>
            
	    </div>
            
	</div>
