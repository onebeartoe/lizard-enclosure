
	<div class="copyspace">
            
	    <h3>Lizard Enclosure - Schedule</h3>           

            <div class="featuredProject">
                <div>
                    ${saveMessage}
                </div>
                <center>
                    current cron schedule:
                    <br/>
<pre>                    
${cronTable}
</pre>              
                    </b>

                    <form method="POST" action="${pageContext.request.contextPath}/schedule/edit">
                        <input type="submit" value="Edit">
                    </form>
                </center>
            
	    </div>
            
	</div>
