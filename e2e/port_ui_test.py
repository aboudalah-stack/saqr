import asyncio,json
from playwright.async_api import async_playwright
async def main():
  async with async_playwright() as p:
    b=await p.chromium.launch(); pg=await b.new_page(viewport={'width':1200,'height':600})
    errs=[]; pg.on('pageerror',lambda e:errs.append(str(e)))
    await pg.route('**/*',lambda r: r.abort() if r.request.url.startswith('http') and 'jsdelivr' not in r.request.url else r.continue_())
    await pg.goto('file:///home/claude/saqr-src/web/_t.html'); await pg.wait_for_timeout(1500)
    hd=open('/home/claude/work/st1.json').read()
    t4={"id":5,"stake":0,"phase":"play","me":0,"turn":0,"dealer":1,"handNo":1,"seats":[{"name":"ayman","score":12,"bid":4,"tk":2,"uid":"a"},{"name":"مازن","score":5,"bid":3,"tk":1,"bot":True},{"name":"سامي","score":9,"bid":2,"tk":1,"bot":True},{"name":"رامي","score":-4,"bid":3,"tk":0,"bot":True}],
        "hand":[0,1,5,13,14,20,26,30,39,41,44],"legal":[0,1,5],"t_seats":[1,2],"t_cards":[3,8],"left":12000,"last":None}
    for name,js in [('p_t4',"S.screen='t400';S.t4={v:%s,busy:false,ver:-1,shown:0,lastKey:'',log:[],turnEnd:Date.now()+9000};"%json.dumps(t4)),
                    ('p_hd',"S.screen='hand';S.hd={variant:'normal',sel:[],stage:[],v:%s,turnEnd:Date.now()+9000};"%hd)]:
      await pg.evaluate("(c)=>__ev(c)","(function(){var sp=document.getElementById('splash');if(sp)sp.remove();S.gm=null;S.modal=null;S.portPref=false;"+js+"fitW=0;fit();renderNow();})()")
      await pg.wait_for_timeout(900); await pg.screenshot(path='/home/claude/work/%s_l.png'%name)
    print(errs); await b.close()
asyncio.run(main())
