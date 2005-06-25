program treecreate;

{$mode objfpc}
{$H+}

uses
  lnxutils, SysUtils;
  
  
type
  PLinkedList = ^TLinkedList;
  TLinkedList = record
    data : string;
    next : PLinkedList;
  end;//TLinkedList
  
  PMainLink = ^TMainLink;
  TMainLink = record
    data  : string;
    depth : integer;
    next  : PMainLink;

    neibours : PLinkedList;
  end;//TMainLink

  
  

var
  cmdcall : TCmdCall;
  options : PPChar;
  depths  : array [1..3] of integer;
  graph : TMainLink;
  current, travers, tail : PMainLink;
  list, iter : PLinkedList;
  i, j : integer;
  oneword : string;
  found : boolean;
  MaxDepth : integer;
begin

  if ParamCount<2 then begin
    writeln('Usage:');
    writeln('    treecreate word output.file');
  end;

  MaxDepth := 3;
  depths[1] := 8;
  depths[2] := 5;
  depths[3] := 3;
  
  graph.data  := ParamStr(1);
  graph.depth := 0;
  graph.next  := nil;
  graph.neibours := nil;
  tail := @graph;
  
  GetMem(options, 9*SizeOf(Pchar));
  options[0] := PChar('/usr/local/bin/associate');
  options[1] := PChar('-m');
  options[2] := PChar('/usr/local/share/infomap-nlp/models');
  options[3] := PChar('-c');
  options[4] := PChar('seattle');
  options[5] := PChar('-n');
  options[6] := PChar('5');
  options[8] := nil;
  
  cmdcall := TCmdCall.Create;
  
  current := @graph;
  repeat
  
    options[7] := PChar(current^.data);
    cmdcall.call(options);
    j := 1;
    for i:=1 to length(cmdcall.OutputStr) do begin
      if cmdcall.OutputStr[i]=#0 then begin
        oneword := trim(Copy(cmdcall.OutputStr, j, i-j));
        if oneword<>'' then begin
          //check if oneword is already in the mainlink
          travers := @graph;
          found := false;
          while travers^.next<>nil do begin
            if travers^.data=oneword then begin
              found := true;
              exit;
            end;
          end;//while
          if (not found) and (current^.depth<MaxDepth) then begin
            travers := new(PMainLink);
            travers^.data := oneword;
            travers^.next := nil;
            travers^.depth := current^.depth+1;
            travers^.neibours := nil;
            tail^.next := travers;
            tail := travers;
          end else begin
            list := new(PLinkedList);
            if current^.neibours=nil then begin
              list^.next := nil;
              list^.data := oneword;
              current^.neibours := list;
            end else begin
              iter := current^.neibours;
              while iter^.next<>nil do begin
                iter := iter^.next;
              end;
              iter^.next := list;
            end;
          end;
        end;
      end;
    end;//for i
    
  until current^.next = nil;


  cmdcall.Free;
  
  FreeMem(options);
end.//program treecreate


