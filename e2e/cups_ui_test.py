import asyncio,sys
from playwright.async_api import async_playwright
async def main():
  async with async_playwright() as p:
    b=await p.chromium.launch(); pg=await b.new_page(viewport={'width':1200,'height':600})
    errs=[]; pg.on('pageerror',lambda e:errs.append(str(e)))
    await pg.route('**/*',lambda r: r.abort() if r.request.url.startswith('http') and 'jsdelivr' not in r.request.url else r.continue_())
    await pg.goto('file:///home/claude/saqr-src/web/_t.html'); await pg.wait_for_timeout(1500)
    pl=[{'slot':i,'name':n,'bot':i!=2,'uid':None,'alive':i in (2,4,6,3),'place':None,'prize':0} for i,n in enumerate(['ريم','سامر','ayman','Joker','نادين','أبو خالد','طارق','Lucky7'])]
    base="""(function(){var sp=document.getElementById('splash');if(sp)sp.remove();S.gm=null;S.modal=null;S.screen='cups';P.chips=5000000;
      S.cu={game:'t400',sel:null,at:Date.now(),list:[{id:1,game:'t400',entry:10000,size:8,status:'open',round:0,pool:72000,startsIn:45000,joined:3,me:null,players:[],tables:[]},
       {id:2,game:'t400',entry:1000000,size:8,status:'open',round:0,pool:7200000,startsIn:null,joined:0,me:null,players:[],tables:[]},
       {id:3,game:'t400',entry:100000000,size:8,status:'open',round:0,pool:720000000,startsIn:null,joined:0,me:null,players:[],tables:[]}]};
      %s renderNow();})()"""
    await pg.evaluate("(c)=>__ev(c)",base%'')
    await pg.wait_for_timeout(300); await pg.screenshot(path='/home/claude/work/c1.png')
    import json
    v={'id':9,'game':'t400','entry':10000,'size':8,'status':'play','round':1,'pool':72000,'startsIn':0,'joined':8,'me':2,'players':pl,
       'tables':[{'round':1,'tno':0,'gid':1,'slots':[0,2,1,4],'done':False,'adv':None},{'round':1,'tno':1,'gid':2,'slots':[3,5,6,7],'done':True,'adv':[3,6]}]}
    await pg.evaluate("(c)=>__ev(c)",base%("S.cu.sel=9;S.cu.v="+json.dumps(v)+";S.cu.vAt=Date.now();"))
    await pg.wait_for_timeout(300); await pg.screenshot(path='/home/claude/work/c2.png')
    print(errs); await b.close()
asyncio.run(main())
